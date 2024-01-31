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

  def createQueue[E](capacity: Z, overflowStrategy: OverflowStrategy.Type): Queue[E] = $

  def createSingletonEventQueue[E](): Queue[E] = $
  def createSingletonDataQueue[E](): Queue[E] = $
}
