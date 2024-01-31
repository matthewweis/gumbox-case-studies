package art

import org.sireum._
import art._

@ext object Queues_Ext {

  def createSingletonEventQueue[E](): Queue[E] = {
    return new QueueSingletonEventPort[E]()
  }
  def createSingletonDataQueue[E](): Queue[E] = {
    return new QueueSingletonDataPort[E]()
  }

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
