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
  val queue: () => Queue[DataContent] =
    () => Queues.createQueue(z"64", OverflowStrategy.DropOldest)

  // infrastructure (service)
  val jms: InfrastructureInOut = InfrastructureCombiner(Infrastructures.jmsIn(), Infrastructures.jmsOut())

  val local: InfrastructureInOut = Infrastructures.local()

  // bundles
  val jmsIn: InPortServiceBundle = InPortServiceBundle(jms, queue)
  val jmsOut: OutPortServiceBundle = OutPortServiceBundle(jms, queue)
  val localIn: InPortServiceBundle = InPortServiceBundle(local, queue)
  val localOut: OutPortServiceBundle = OutPortServiceBundle(local, queue)

}
