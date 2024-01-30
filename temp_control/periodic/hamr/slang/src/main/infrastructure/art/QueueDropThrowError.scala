package art

import org.sireum._
import art._

import java.util.concurrent.ConcurrentLinkedQueue

class QueueDropThrowError[E](capacity: Int) extends Queue[E] {

//  val target: SpscAtomicArrayQueue[E] = new SpscAtomicArrayQueue(capacity)
val target: ConcurrentLinkedQueue[E] = new ConcurrentLinkedQueue() // todo maximum safety during debug

  override def peek(): Option[E] = {
    val r = target.peek()
    if (r != null) Some(r) else None()
  }

  override def drain(consumer: E => Unit): Unit = {
    target.forEach((e: E) => consumer(e))
  }

  override def drainWithLimit(consumer: E => Unit, limit: Z): Unit = {
//    target.drain((e: E) => consumer(e), limit.toInt)
    var i = 0
    while (i < limit && !target.isEmpty) {
      val e = target.poll()
      consumer(e)
      i = i + 1
    }
  }

  override def offer(e: E): B = {
    if (!target.offer(e)) {
      throw new RuntimeException(s"Queue with 'error-on-drop' policy was forced to drop $e.")
    }
    T
  }

  override def isEmpty(): B = target.isEmpty()

  override def string: String = super.toString()
}
