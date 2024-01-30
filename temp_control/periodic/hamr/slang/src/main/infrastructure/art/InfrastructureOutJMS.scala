package art

import art.Art.PortId
import art.InfrastructureRegistry.PortTopic
import art._
import org.sireum
import Serde._

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
