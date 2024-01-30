package art

import org.sireum._
import art._

import java.util.concurrent.ConcurrentLinkedQueue

class QueueDropOldest[E](capacity: Int) extends Queue[E] {

  /**
   * We can't use SPSC queue because the offering thread must be available to drop oldest to insert newest as needed.
   *
   * Ideal configuration would be a concurrent circular buffer backed queue for SPSC (if possible),
   *   or a "consumer thread work-stealing" solution.
   *
   * This is still more than likely fast enough.
   */
//  val target: SpmcAtomicArrayQueue[E] = new SpmcAtomicArrayQueue(capacity)
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
    if (!target.offer(e)) { // try to offer to queue (fails if queue is full) (todo actually if or iff? we want iff)
      // todo confirm consumer can be producer with SPMC
      val dropped = target.poll() // drop oldest
      println(s"#DROPPED OLDEST VALUE $dropped")
      return target.offer(e) // insert newest. if it fails this time then either:
        //   (1) queue capacity is 0,
        //   (2) queue is being incorrectly accessed from multiple producer threads
        // in either case, ignore (todo consider logging an error if result of second target.offer is false AND capacity > 0)
    }
    T // if here then the first target.offer was successful (hence we return true)
  }

  override def isEmpty(): B = target.isEmpty

  override def string: String = super.toString()
}
