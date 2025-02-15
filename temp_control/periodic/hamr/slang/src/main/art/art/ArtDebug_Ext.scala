package art

import org.sireum._
import art.Art.Time
import scala.collection.mutable.{Map => MMap, Set => MSet}

object ArtDebug_Ext {
  private val debugObjects: MMap[String, Any] = ArtNative_Ext.concMap()
  private val listeners: MSet[ArtListener] = concSet()

  protected[art] def start(): Unit = {
    val time = Art.time()
    listeners.foreach((listener: ArtListener) => listener.start(time))
  }

  protected[art] def stop(): Unit = {
    val time = Art.time()
    listeners.foreach((listener: ArtListener) => listener.stop(time))
  }

  protected[art] def outputCallback(src: Art.PortId, dst: Art.PortId, data: DataContent, time: Time): Unit = {
    listeners.foreach((listener: ArtListener) => listener.output(src, dst, data, time))
  }

  def setDebugObject[T](key: String, o: T): Unit = {
    ArtNative.logDebug(Art.logTitle, s"Set debug object for $key")
    debugObjects(key) = o
  }

  def getDebugObject[T](key: String): Option[T] = {
    debugObjects.get(key) match {
      case scala.Some(o) => Some(o.asInstanceOf[T])
      case _ => None[T]()
    }
  }

  def injectPort(bridgeId: Art.BridgeId, port: Art.PortId, data: DataContent): Unit = {

    val bridge = Art.bridges(bridgeId.toZ).get

    if (bridge.ports.dataOuts.elements.map(_.id).contains(port) ||
      bridge.ports.eventOuts.elements.map(_.id).contains(port)) {

      ArtNative.logDebug(Art.logTitle, s"Injecting from port ${Art.ports(port).get.name}")

      ArtNative.putValue(port, data)

      ArtNative.sendOutput(bridge.ports.eventOuts.map(_.id), bridge.ports.dataOuts.map(_.id))
    } else {
      ArtNative.logDebug(Art.logTitle, s"Injecting to port ${Art.ports(port).get.name}")

      // right now, there is no difference between treatment of data and event ports, but keep the logic
      // separate for further refactoring
      if (bridge.ports.dataIns.elements.map(_.id).contains(port)) {
        // assume inInfrastructure Dequeue can be cast to a 2-way queue.
        // If not then it is probably a network queue and may not be possible to inject into
        ArtNative_Ext.inInfrastructurePorts(port.toZ).asInstanceOf[Queue[ArtMessage]].offer(ArtMessage(data))
      } else {
        ArtNative_Ext.inInfrastructurePorts(port.toZ).asInstanceOf[Queue[ArtMessage]].offer(ArtMessage(data))
      }
    }
  }

  def registerListener(listener: ArtListener): Unit = {
    listeners.add(listener)
  }

  def concSet[K](): MSet[K] = {
    import org.sireum.$internal.CollectionCompat.Converters._
    val m: java.util.Set[K] = java.util.concurrent.ConcurrentHashMap.newKeySet()
    m.asScala
  }
}