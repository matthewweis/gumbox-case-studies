// #Sireum

package tc

import org.sireum._
import art._
import art.Queues._
import art.Infrastructures._
import art.DispatchPropertyProtocol._
import art.PortMode._

// This file was auto-generated.  Do not edit

// Note that custom scala versions must be removed from project's versions.properties
// to avoid NoSuchMethodError in scala.tools.nsc.reporters.Reporter
object DemoUtils {

  // queue (service)
  // WARNING: DATA QUEUE USING EVENT
  val dataQueue: () => Queue[DataContent] = () => Queues.createSingletonDataQueue()
  val eventQueue: () => Queue[DataContent] = () => Queues.createSingletonEventQueue()

  // infrastructure
  val jms: InfrastructureInOut = InfrastructureCombiner(Infrastructures.jmsIn(), Infrastructures.jmsOut())
  val local: InfrastructureInOut = Infrastructures.local()

  def bundleIn(infrastructure: InfrastructureIn, queueSupplier: () => Queue[DataContent]): InPortServiceBundle = {
    return InPortServiceBundle(infrastructure, queueSupplier)
  }

  def bundleOut(infrastructure: InfrastructureOut, queueSupplier: () => Queue[DataContent]): OutPortServiceBundle = {
    return OutPortServiceBundle(infrastructure, queueSupplier)
  }

}
