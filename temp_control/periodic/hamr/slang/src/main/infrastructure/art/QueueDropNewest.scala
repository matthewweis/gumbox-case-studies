package art

import org.sireum._
//import org.jctools.queues.atomic.{MpmcAtomicArrayQueue, SpscAtomicArrayQueue}
import art._

import java.util.concurrent.ConcurrentLinkedQueue

class QueueDropNewest[E](capacity: Int) extends Queue[E] {

//  val target: SpscAtomicArrayQueue[E] = new SpscAtomicArrayQueue(capacity)
//  val target: MpmcAtomicArrayQueue[E] = new MpmcAtomicArrayQueue(capacity) // todo maximum safety during debug
  val target: ConcurrentLinkedQueue[E] = new ConcurrentLinkedQueue() // todo maximum safety during debug


  override def peek(): Option[E] = {
    val r = target.peek()
    if (r != null) Some(r) else None()
  }

  override def drain(consumer: E => Unit): Unit = {
    target.forEach((e: E) => consumer(e))
  }

  override def offer(e: E): B = {
    val isFull = !target.offer(e) // if false then the queue was full and "e" was not inserted (which is the desired behavior)
    if (isFull) {
      println(s"#DROPPED NEWEST VALUE $e")
    }
    T // always return true because either e was entered into queue or it wasn't (in which case the latest was dropped)
  }

  override def drainWithLimit(consumer: E => Unit, limit: Z): Unit = {
    var i = 0
    while (i < limit && !target.isEmpty) {
      val e = target.poll()
      consumer(e)
      i = i + 1
    }
//    target.drain((e: E) => consumer(e), limit.toInt)
  }

  override def isEmpty(): B = target.isEmpty()

  override def string: String = super.toString()
}
