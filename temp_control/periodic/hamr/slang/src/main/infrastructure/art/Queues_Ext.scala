package art

import org.sireum._
import art._

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

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

  //
  // Singleton Queues conforming to PortMode policies
  //

  // thread-safe, holds a single value, value is NOT cleared when "drained"
  class QueueSingletonDataPort[E]() extends Queue[E] {

    val target = new AtomicReference[E]()

    override def peek(): Option[E] = {
      val e = target.get()
      return if (e != null) Some(e) else None()
    }

    override def drain(consumer: E => Unit): Unit = {
      // data ports don't "drain" on being read
      val e = target.get()
      if (e != null) consumer(e)
    }

    override def offer(e: E): B = {
      // overwrite single data value
      target.set(e)
      return T
    }

    override def drainWithLimit(consumer: E => Unit, limit: Z): Unit = {
      if (limit > z"0") drain(consumer)
    }

    override def isEmpty(): B = target.get() == null

    override def string: String = super.toString()
  }

  // thread-safe, holds a single value, value is cleared when "drained"
  class QueueSingletonEventPort[E]() extends Queue[E] {

    val target = new AtomicReference[E]()

    override def peek(): Option[E] = {
      val e = target.get()
      return if (e != null) Some(e) else None()
    }

    override def drain(consumer: E => Unit): Unit = {
      // event ports "drain" on being read
      val e = target.getAndSet(null.asInstanceOf[E])
      if (e != null) consumer(e)
    }

    override def offer(e: E): B = {
      // overwrite single data value
      target.set(e)
      return T
    }

    override def drainWithLimit(consumer: E => Unit, limit: Z): Unit = {
      if (limit > z"0") drain(consumer)
    }

    override def isEmpty(): B = target.get() == null

    override def string: String = super.toString()
  }

  //
  // Sized Queues with overflow policy
  // todo Sized Queues currently unused as capacity is currently ignored while dropping JCTools queues and deciding api
  //
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

  //
  // Ext-only wrapper queues used by ArtNative
  //

  class InfrastructureInPortQueueWrapper(portId: Art.PortId, target: Dequeue[DataContent]) extends Dequeue[ArtMessage] with Queue[ArtMessage] {

    def wrap(data: DataContent): ArtMessage = {
      return ArtMessage(data = data, srcPortId = Some(portId), putValueTimestamp = Art.time())
    }

    override def peek(): Option[ArtMessage] = {
      return target.peek().map((data: DataContent) => wrap(data))
    }

    override def drain(consumer: ArtMessage => Unit): Unit = {
      target.drain((data: DataContent) => consumer(wrap(data)))
    }

    override def drainWithLimit(consumer: ArtMessage => Unit, limit: Z): Unit = {
      target.drainWithLimit((data: DataContent) => consumer(wrap(data)), limit)
    }

    override def isEmpty(): B = target.isEmpty()

    override def string: String = super.toString()

    // DEBUG
    override def offer(e: ArtMessage): B = target.asInstanceOf[Queue[DataContent]].offer(e.data)
  }

  class InfrastructureOutPortQueueWrapper(portId: Art.PortId, target: Enqueue[DataContent]) extends Enqueue[ArtMessage] with Queue[ArtMessage] {

    // since this queue holds a single value, always override any existing
    override def offer(e: ArtMessage): B = {
      return target.offer(e.data)
    }

    override def string: String = super.toString()

    // DEBUG
    override def drain(consumer: ArtMessage => Unit): Unit = target.asInstanceOf[Queue[DataContent]].drain((d: DataContent) => { consumer(ArtMessage(d)) })

    // DEBUG
    override def drainWithLimit(consumer: ArtMessage => Unit, limit: Z): Unit = target.asInstanceOf[Queue[DataContent]].drainWithLimit((d: DataContent) => { consumer(ArtMessage(d)) }, limit)

    // DEBUG
    override def isEmpty(): B = target.asInstanceOf[Queue[DataContent]].isEmpty()

    // DEBUG
    override def peek(): Option[ArtMessage] = target.asInstanceOf[Queue[DataContent]].peek().map((d: DataContent) => { ArtMessage(d) })
  }


}
