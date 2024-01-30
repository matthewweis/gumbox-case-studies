package art

import art._

object Infrastructures_Ext {

  def jmsIn(): InfrastructureIn = {
    return new InfrastructureInJMS()
  }

  def jmsOut(): InfrastructureOut = {
    return new InfrastructureOutJMS()
  }

  def local(): InfrastructureInOut = {
    return new InfrastructureLocal()
  }

}
