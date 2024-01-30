package art

import org.sireum._
import art._

@ext object Queues_Ext {

  /*
  val inInfrastructurePorts: MMap[Z, ArtMessage] = concMap()
  val outInfrastructurePorts: MMap[Z, ArtMessage] = concMap()
  val inPortVariables: MMap[Z, ArtMessage] = concMap()
  val outPortVariables: MMap[Z, ArtMessage] = concMap()
   */
//  def inInfrastructurePortsQueueWrapper[E](portId: Z): Queue[E] = {
//    return new QueueConcMap[ArtMessage](portId, ArtNative_Ext.inInfrastructurePorts)
//  }
//  def outInfrastructurePortsQueueWrapper[E](portId: Z): Queue[E] = {
//    return new QueueConcMap[ArtMessage](portId, ArtNative_Ext.outInfrastructurePorts)
//  }
//  def inPortVariablesQueueWrapper[E](portId: Z): Queue[E] = {
//    return new QueueConcMap[ArtMessage](portId, ArtNative_Ext.inPortVariables)
//  }
//  def outPortVariablesQueueWrapper[E](portId: Z): Queue[E] = {
//    return new QueueConcMap[ArtMessage](portId, ArtNative_Ext.outPortVariables)
//  }

  def createQueue[E](capacity: Z, overflowStrategy: OverflowStrategy.Type): Queue[E] = {
    return overflowStrategy match {
      case OverflowStrategy.DropOldest => new QueueDropOldest(capacity.toInt)
      case OverflowStrategy.DropNewest => new QueueDropNewest(capacity.toInt)
      case OverflowStrategy.Error => new QueueDropThrowError(capacity.toInt)
    }
//    if (overflowStrategy == OverflowStrategy.DropOldest) {
//      new QueueDropOldest(capacity)
//    } else if (overflowStrategy == OverflowStrategy.DropOldest) {
//      new QueueDropNewest(capacity)
//    } else {
//      new QueueDropThrowError(capacity)
//    }
  }
}
