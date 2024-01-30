package art

import art.Art.PortId
import org.sireum._
import org.sireum.B
import art.InfrastructureRegistry.PortTopic
import art._
import Serde._
import java.util.concurrent.{ConcurrentMap => MMap}

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
