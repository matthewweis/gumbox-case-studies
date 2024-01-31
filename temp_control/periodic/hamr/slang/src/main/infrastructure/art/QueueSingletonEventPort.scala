package art

import org.sireum._

import java.util.concurrent.atomic.AtomicReference

// thread-safe, holds a single value, value is cleared when "drained"
class QueueSingletonEventPort[E]() extends Queue[E] {

  val target = new AtomicReference[E]()

  override def peek(): Option[E] = {
    val e = target.get()
    return if (e != null) Some(e) else None()
  }

  override def drain(consumer: E => Unit): Unit = {
    // data ports don't "drain" on being read
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
