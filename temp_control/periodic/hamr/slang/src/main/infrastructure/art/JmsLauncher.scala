package art

import org.apache.activemq.broker.BrokerService
import org.apache.activemq.{ActiveMQConnection, ActiveMQConnectionFactory}
import org.sireum.{None, Option}

import javax.jms.{JMSException, Session}

// launch JMS infrastructure. This never should have been in the Arch in the first place...
object JmsLauncher {

  val TOPIC = "ART_JMS_TOPIC"
  val BIND_ADDRESS = "tcp://localhost:61616"

  def main(args: Array[String]): Unit = {
    val service = new BrokerService
    service.setPersistent(false)
    service.addConnector(BIND_ADDRESS)
    service.setUseLocalHostBrokerName(true)
    service.start()
    service.waitUntilStarted()
    service.waitUntilStopped()
  }

}
