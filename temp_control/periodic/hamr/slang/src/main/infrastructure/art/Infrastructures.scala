// #Sireum

package art

import org.sireum._
import art._

@ext object Infrastructures {
  def jmsIn(): InfrastructureIn = $
  def jmsOut(): InfrastructureOut = $
  def local(): InfrastructureInOut = $
}
