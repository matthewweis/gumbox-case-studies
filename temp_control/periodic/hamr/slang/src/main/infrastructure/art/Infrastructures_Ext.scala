package art

import org.sireum._
import art.Art.PortId
import art.InfrastructureRegistry.PortTopic
import art.Serde._
import org.apache.activemq.{ActiveMQConnection, ActiveMQConnectionFactory}
import org.sireum

import java.util.concurrent.{Executors, ScheduledExecutorService, ConcurrentMap => MMap}
import javax.jms.{JMSException, Message, MessageConsumer, MessageListener, MessageProducer, Session, TextMessage}

object Infrastructures_Ext {

  def jmsIn(ad: ArchitectureDescription): InfrastructureIn = {
    return new InfrastructureInJMS(ad)
  }

  def jmsOut(ad: ArchitectureDescription): InfrastructureOut = {
    return new InfrastructureOutJMS(ad)
  }

  def localIn(ad: ArchitectureDescription): InfrastructureIn = {
    return new InfrastructureLocalIn(ad)
  }

  def localOut(ad: ArchitectureDescription): InfrastructureOut = {
    return new InfrastructureLocalOut(ad)
  }

  def localFused(from: Art.PortId, to: Art.PortId, ad: ArchitectureDescription): Fuseable = {
    return new InfrastructureLocalFused(from: Art.PortId, to: Art.PortId, ad)
  }

  def resetCaches(): Unit = {
    InfrastructureLocal.map_FanOutEnqueue_Cache.clear()
    InfrastructureLocal.localCache.clear()
  }
}

object InfrastructureInJMS {
  val map: MMap[PortTopic, InfrastructureAsyncQueueForwardingConsumer] = javaConcurrentMap()

  private def javaConcurrentMap[K, V](): java.util.concurrent.ConcurrentMap[K, V] = {
    new java.util.concurrent.ConcurrentHashMap[K, V].asInstanceOf[java.util.concurrent.ConcurrentMap[K, V]]
  }
}

class InfrastructureInJMS(ad: ArchitectureDescription) extends InfrastructureIn {

  val deserializer: Deserializer = Serde.jsonDeserializer()

  // called once per port
  override def startInfrastructureIn(portId: PortId, producer: Enqueue[DataContent]): Unit = {
    val topic: PortTopic = InfrastructureRegistry.portToTopic(portId, ad)
    val map = InfrastructureInJMS.map

    // Create async JMS sender for the port topic.
    // If multiple ports share a topic (fan out), it will be created once and shared by those ports.
    map.computeIfAbsent(topic, (key: PortTopic) => {
      val topicString = key.toString
      val wrapper = DeserializingEnqueue(producer, deserializer)
      InfrastructureAsyncQueueForwardingConsumer.fromTopic(topicString, wrapper)
    })
  }

  override def string: String = this.toString

}

class InfrastructureAsyncQueueForwardingConsumer(session: Session, consumer: MessageConsumer, queue: Enqueue[String]) {

  // queue intentionally unused (see setupQueueForwarding(...) in object below for queue usage)

  def close(): Unit = {
    session.close()
    consumer.close()
  }

}

object InfrastructureAsyncQueueForwardingConsumer {

  @throws[JMSException]
  private def createSession() = {
    val connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL)
    val connection = connectionFactory.createConnection
    connection.start()
    connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
  }

  @throws[JMSException]
  private def setupQueueForwarding(messageConsumer: MessageConsumer, queue: Enqueue[String]): Unit = {
    messageConsumer.setMessageListener(new MessageListener() {
      override def onMessage(message: Message): Unit = {
        try message match {
          case textMessage: TextMessage =>
            val text = textMessage.getText
            queue.offer(text)
          case _ =>
        }
        catch {
          case e: JMSException =>
            throw new RuntimeException("onMessage: " + e)
        }
      }
    })
  }

  def fromTopic(topic: org.sireum.String, queue: Enqueue[org.sireum.String]): InfrastructureAsyncQueueForwardingConsumer = try {
    val session = createSession()
    // destination = "on the server" -- so this is remote
    val destination = session.createTopic(topic.toString())
    // lets us take from queue
    val messageConsumer = session.createConsumer(destination)
    setupQueueForwarding(messageConsumer, queue)
    return new InfrastructureAsyncQueueForwardingConsumer(session, messageConsumer, queue)
  } catch {
    case e: JMSException =>
      throw new RuntimeException(e)
  }
}

class InfrastructureOutJMS(ad: ArchitectureDescription) extends InfrastructureOut {

  val serializer: Serializer = Serde.jsonSerializer()

  // Return interface to outbound JMS (represented as Enqueue).
  def startInfrastructureOut(portId: PortId): Enqueue[DataContent] = {
    val topic: PortTopic = InfrastructureRegistry.portToTopic(portId, ad)

    // note: InfrastructureSyncProducer implements Enqueue[org.sireum.String]
    val producer: Enqueue[org.sireum.String] = InfrastructureSyncProducer.fromTopic(topic.toString())
    return SerializingEnqueue(producer, serializer)
  }

  override def string: sireum.String = this.toString
}

object InfrastructureLocal {
  val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
  val map_FanOutEnqueue_Cache: MMap[PortTopic, ISZ[Enqueue[DataContent]]] = javaConcurrentMap() // todo rm this and FanOutEnqueue?

  // map by dst instead of topic
  val localCache: MMap[Z, Enqueue[DataContent]] = javaConcurrentMap()

  private def javaConcurrentMap[K, V](): java.util.concurrent.ConcurrentMap[K, V] = {
    new java.util.concurrent.ConcurrentHashMap[K, V].asInstanceOf[java.util.concurrent.ConcurrentMap[K, V]]
  }

  class FanOutEnqueue(portId: PortId) extends Enqueue[DataContent] with Queue[DataContent] {
    override def offer(d: DataContent): B = {
      last = Some(d) // for DEBUG / TEST
      var r = true
      // todo replace map with ArtNative_Ext calls?
      if (map_FanOutEnqueue_Cache.get(portId) == null) {
        println("HERE!")
      }
      map_FanOutEnqueue_Cache.get(portId).foreach((q: Enqueue[DataContent]) => {
        r = r && q.offer(d)
      })
      return r
    }

    override def string: String = super.toString

    // DEBUG
    var last: Option[DataContent] = None()

    // DEBUG
    override def drain(consumer: DataContent => Unit): Unit = last.foreach((d: DataContent) => consumer(d))

    // DEBUG
    override def drainWithLimit(consumer: DataContent => Unit, limit: Z): Unit = if (limit > z"0") drain(consumer)

    // DEBUG
    override def isEmpty(): B =  last.isEmpty

    // DEBUG
    override def peek(): Option[DataContent] = last
  }

}

/*
 * Infrastructure for local in port. Does not require any fusion or connections. Simply receives DataContent from
 * incoming infrastructureInQueue(s). Forwarding routes are tracked in InfrastructureLocal#localCache and routes are
 * updated when infrastructure launches.
 */
class InfrastructureLocalIn(ad: ArchitectureDescription) extends InfrastructureIn {

  override def startInfrastructureIn(portId: PortId, infrastructureEnqueue: Enqueue[DataContent]): Unit = {
    val topic: PortTopic = InfrastructureRegistry.portToTopic(portId, ad) // topic = portId of OUT port
    val connections: IS[Art.ConnectionId, UConnection] = ad.connections
    for (connection <- connections if connection.to.id == portId) {
      require(topic == connection.from.id, st"SANITY CHECK FAILED! topic $topic should equal connection.from ${connection.from}")
      InfrastructureLocal.map_FanOutEnqueue_Cache.merge(topic, ISZ(infrastructureEnqueue), (a, b) => a ++ b)

      require(!InfrastructureLocal.localCache.containsKey(portId.toZ), "duplicate key!")
      InfrastructureLocal.localCache.put(portId.toZ, infrastructureEnqueue)
    }
  }

  override def string: sireum.String = this.toString
}

/*
 * Infrastructure for local out port. Does not require any fusion or connections. Simply forwards DataContent into
 * the port's infrastructureInQueue(s). Forwarding routes are tracked in InfrastructureLocal#localCache and routes are
 * updated when infrastructure launches.
 */
class InfrastructureLocalOut(ad: ArchitectureDescription) extends InfrastructureOut {

  class InfrastructureLocalOutQueue(portId: PortId) extends Queue[DataContent] with Enqueue[DataContent] with Dequeue[DataContent] {
    override def offer(e: DataContent): B = {
      last = Some(e) // <-- used to enable "peaking" at infrastructureOut implementations with no backing queue or connected destinations.
                     //     for example: FanOut to N uninitialized/excluded bridges. This is for testing(/debugging) only.
                     //     All other Dequeue functions below (marked "// DEBUG") are for the same reason.
      var offered = false
      for (connection <- ad.connections if connection.from.id == portId) {
        val to = InfrastructureLocal.localCache.get(connection.to.id.toZ)
        if (to != null) {
          offered = true
          to.offer(e)
        }
      }
      offered
    }
    // DEBUG
    var last: Option[DataContent] = None()
    // DEBUG
    override def drain(consumer: DataContent => Unit): Unit = last.foreach((d: DataContent) => consumer(d))
    // DEBUG
    override def drainWithLimit(consumer: DataContent => Unit, limit: Z): Unit = if (limit > z"0") drain(consumer)
    // DEBUG
    override def isEmpty(): B =  last.isEmpty
    // DEBUG
    override def peek(): Option[DataContent] = last

    override def string: sireum.String = this.toString
  }

  override def startInfrastructureOut(portId: PortId): Enqueue[DataContent] = {
    // InfrastructureLocal.FanOutEnqueue(portId) is now legacy, probably will remove
    return new InfrastructureLocalOutQueue(portId)
  }

  override def string: sireum.String = this.toString

}

class InfrastructureLocalFused(from: Art.PortId, to: Art.PortId, ad: ArchitectureDescription) extends Fuseable with InfrastructureIn with InfrastructureOut {

  // shared queue for 1-to-1 local connection. Queue is set by infrastructureIn's bundle.
  private var fused: Queue[DataContent] = null

  override def startInfrastructureIn(portId: PortId, infrastructureEnqueue: Enqueue[DataContent]): Unit = {
    assert(to == portId, st"startInfrastructureIn: portId $portId != to $to".render)
    assert(infrastructureEnqueue.isInstanceOf[Dequeue[_]], "startInfrastructureIn: fused infrastructure must be backed by a Queue (Enqueue+Dequeue)")
    assert((for (c <- ad.connections if c.to.id == portId) yield c).size == z"1", "fusable connections must be 1-to-1 and backed by a local Queue")
    fused = infrastructureEnqueue.asInstanceOf[Queue[DataContent]]
  }

  override def startInfrastructureOut(portId: PortId): Enqueue[DataContent] = {
    assert(portId == from, st"startInfrastructureOut: portId $portId != from $from".render)
    assert(fused != null, st"startInfrastructureOut: must start infrastructure in before out $portId for fused queues".render)
    return fused
  }

  override def string: sireum.String = this.toString
}

class InfrastructureSyncProducer(session: Session, producer: MessageProducer) extends Enqueue[org.sireum.String] {

  override def offer(message: sireum.String): B = {
    try {
      val textMessage = session.createTextMessage(message.toString())
      producer.send(textMessage)
    } catch {
      case e: JMSException =>
        throw new RuntimeException(e) // could return F also?
    }
    return T
  }

  def close(): Unit = {
    session.close()
    producer.close()
  }

  override def string: sireum.String = this.toString
}

object InfrastructureSyncProducer {

  def fromTopic(topic: String): InfrastructureSyncProducer = {
    try {
      val session = createSession()
      // destination = "on the server" -- so this is remote
      val destination = session.createTopic(topic.toString())

      // lets us send to queue
      val producer = session.createProducer(destination)

      new InfrastructureSyncProducer(session, producer)
    } catch {
      case e: JMSException =>
        throw new RuntimeException(e)
    }
  }

  @throws[JMSException]
  private def createSession(): Session = {
    val connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL)
    val connection = connectionFactory.createConnection
    connection.start()
    connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
  }

}