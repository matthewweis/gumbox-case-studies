// #Sireum

package art

import org.sireum._
import art._

@enum object OverflowStrategy {
  'DropOldest
  'DropNewest
  'Error
}

@ext object Queues {

//  def inInfrastructurePortsQueueWrapper[E](portId: Z): Queue[E] = $
//  def outInfrastructurePortsQueueWrapper[E](portId: Z): Queue[E] = $
//  def inPortVariablesQueueWrapper[E](portId: Z): Queue[E] = $
//  def outPortVariablesQueueWrapper[E](portId: Z): Queue[E] = $

  def createQueue[E](capacity: Z, overflowStrategy: OverflowStrategy.Type): Queue[E] = $
}
