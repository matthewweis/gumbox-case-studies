package art

import org.sireum._
import org.sireum
import art.Art.PortId
import art.ArtNative_Ext.concMap
import art.InfrastructureRegistry.PortTopic
import art._
import org.sireum.ops.ISZOps

import java.util
import java.util.concurrent.{ConcurrentMap => MMap}
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

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
