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

  def jmsIn(): InfrastructureIn = {
    return new InfrastructureInJMS()
  }

  def jmsOut(): InfrastructureOut = {
    return new InfrastructureOutJMS()
  }

  def local(): InfrastructureInOut = {
    return new InfrastructureLocal()
  }

}

object InfrastructureInJMS {
  val map: MMap[PortTopic, InfrastructureAsyncQueueForwardingConsumer] = javaConcurrentMap()

  private def javaConcurrentMap[K, V](): java.util.concurrent.ConcurrentMap[K, V] = {
    new java.util.concurrent.ConcurrentHashMap[K, V].asInstanceOf[java.util.concurrent.ConcurrentMap[K, V]]
  }
}

class InfrastructureInJMS extends InfrastructureIn {

  val deserializer: Deserializer = Serde.jsonDeserializer()

  // called once per port
  override def startInfrastructureIn(portId: PortId, producer: Enqueue[DataContent]): Unit = {
    val topic: PortTopic = InfrastructureRegistry.portToTopic(portId)
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

class InfrastructureOutJMS() extends InfrastructureOut {

  val serializer: Serializer = Serde.jsonSerializer()

  // Return interface to outbound JMS (represented as Enqueue).
  def startInfrastructureOut(portId: PortId): Enqueue[DataContent] = {
    val topic: PortTopic = InfrastructureRegistry.portToTopic(portId)

    // note: InfrastructureSyncProducer implements Enqueue[org.sireum.String]
    val producer: Enqueue[org.sireum.String] = InfrastructureSyncProducer.fromTopic(topic.toString())
    return SerializingEnqueue(producer, serializer)
  }

  override def string: sireum.String = this.toString
}

object InfrastructureLocal {
  val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
  val map: MMap[PortTopic, ISZ[Enqueue[DataContent]]] = javaConcurrentMap()

  private def javaConcurrentMap[K, V](): java.util.concurrent.ConcurrentMap[K, V] = {
    new java.util.concurrent.ConcurrentHashMap[K, V].asInstanceOf[java.util.concurrent.ConcurrentMap[K, V]]
  }

  class FanOutEnqueue(portId: PortId) extends Enqueue[DataContent] {
    override def offer(d: DataContent): B = {
      var r = true
      map.get(portId).foreach((q: Enqueue[DataContent]) => {
        r = r && q.offer(d)
      })
      return r
    }

    override def string: String = super.toString
  }

}

class InfrastructureLocal extends InfrastructureInOut with InfrastructureIn with InfrastructureOut {

  @pure def seqContains(s: IS[Art.ConnectionId, Art.PortId], e: Art.PortId): B = {
    for (v <- s) {
      if (v == e) {
        return T
      }
    }
    return F
  }

  override def startInfrastructureIn(portId: PortId, infrastructureEnqueue: Enqueue[DataContent]): Unit = {
    val topic: PortTopic = InfrastructureRegistry.portToTopic(portId) // topic = portId of OUT port
    var sanityCheck = false // checks for illegal fan-in connection and fails fast if they exist
    for (i <- Art.connections.indices) { // for each connection
      if (seqContains(Art.connections(i), portId)) { // if filtered
        if (sanityCheck) throw new RuntimeException(s"Multiple connections detected for in port $portId (fan-in is not supported by HAMR)")
        val map = InfrastructureLocal.map
        map.merge(topic, ISZ(infrastructureEnqueue), (a, b) => a ++ b)
        sanityCheck = true // indicate a connection was found
      }
    }
  }

  override def startInfrastructureOut(portId: PortId): Enqueue[DataContent] = {
    val topic: PortTopic = InfrastructureRegistry.portToTopic(portId) // redundant as infOut IS portId
    if (topic != portId) throw new RuntimeException("Expecting topics to be named the outgoing portId (no fan-in).")
    return new InfrastructureLocal.FanOutEnqueue(portId)
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