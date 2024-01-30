package art

import art.Enqueue
import org.apache.activemq.{ActiveMQConnection, ActiveMQConnectionFactory}
import org.sireum
import org.sireum.{B, T}

import javax.jms.{JMSException, MessageProducer, Session}

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
      val destination = session.createTopic(topic)

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
