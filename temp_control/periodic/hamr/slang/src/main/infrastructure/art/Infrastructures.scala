// #Sireum

package art

import org.sireum._
import art._

@ext object Infrastructures {
  def jmsIn(ad: ArchitectureDescription): InfrastructureIn = $
  def jmsOut(ad: ArchitectureDescription): InfrastructureOut = $

  def localIn(ad: ArchitectureDescription): InfrastructureIn = $
  def localOut(ad: ArchitectureDescription): InfrastructureOut = $

  def localFused(from: Art.PortId, to: Art.PortId, ad: ArchitectureDescription): Fuseable = $

  def resetCaches(): Unit = $ // called by SystemTopology.launch
}
