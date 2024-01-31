package art

import org.sireum._
import art._
import org.apache.activemq.{ActiveMQConnection, ActiveMQConnectionFactory}

import javax.jms.{JMSException, Message, MessageConsumer, MessageListener, Session, TextMessage}

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
