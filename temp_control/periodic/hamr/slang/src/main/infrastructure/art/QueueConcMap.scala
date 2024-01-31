package art

import org.sireum._

class InfrastructureInPortQueueWrapper(portId: Art.PortId, target: Dequeue[DataContent]) extends Dequeue[ArtMessage] {

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
}

class InfrastructureOutPortQueueWrapper(portId: Art.PortId, target: Enqueue[DataContent]) extends Enqueue[ArtMessage] {

  // since this queue holds a single value, always override any existing
  override def offer(e: ArtMessage): B = {
    return target.offer(e.data)
  }

  override def string: String = super.toString()
}

//class QueueConcMap(portId: Z, target: Queue[DataContent]) extends Queue[ArtMessage] {
//
//  def wrap(data: DataContent): ArtMessage = {
//    return ArtMessage(data = data, srcPortId = Some(portId), putValueTimestamp = Art.time())
//  }
//
//  override def peek(): Option[ArtMessage] = {
//    return target.peek().map((data: DataContent) => wrap(data))
//  }
//
//  override def drain(consumer: ArtMessage => Unit): Unit = {
//    target.drain((data: DataContent) => consumer(wrap(data)))
//  }
//
//  // since this queue holds a single value, always override any existing
//  override def offer(e: ArtMessage): B = {
//    return target.offer(e.data)
//  }
//
//  override def drainWithLimit(consumer: ArtMessage => Unit, limit: Z): Unit = {
//    target.drainWithLimit((data: DataContent) => consumer(wrap(data)), limit)
//  }
//
//  override def isEmpty(): B = target.isEmpty()
//
//  override def string: String = super.toString()
//}
