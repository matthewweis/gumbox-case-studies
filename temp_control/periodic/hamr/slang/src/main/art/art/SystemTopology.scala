// #Sireum

package art

import org.sireum._
import art._
// import art.Art.{PortId, ports}
import org.sireum.ops.ISZOps

/*
 * INFRASTRUCTURE APIS
 */

@sig trait InfrastructureIn {
  /**
   * Bootstraps the infrastructure of a particular out port.
   * This method occurs BEFORE startInfrastructureOut() is called for its upstream producer. // todo synchronize this across networks
   *
   * @param portId the id of the port to bootstrap
   * @param producer a non-blocking Enqueue to place incoming DataContent into
   */
  def startInfrastructureIn(portId: Art.PortId, producer: Enqueue[DataContent]): Unit
}

@sig trait InfrastructureOut {
  // Replaced Dequeue arg with Enqueue return.
  // The new direction makes the outgoing barrier (potentially) optional
  //
  // note: "art" holds local variable queues
  //                                     [       Queue       ]           [       Queue       ]
  //     - old:    (InfrastructureIn) <- [Enqueue] + [Dequeue] -> art <- [Enqueue] + [Dequeue] -> (InfrastructureOut)
  //                                         (memory fence)                 (memory fence)
  //
  //                                     [       Queue       ]
  //     - new:    (InfrastructureIn) <- [Enqueue] + [Dequeue] -> art -> [Enqueue] -> (InfrastructureOut)
  //                                         (memory fence)
  //
  //     - The new version still supports a second memory barrier and async polling, but leaves it up to the infrastructure out to decide.
  //       - Note: we don't give InfrastructureIn a similar level of control because:
  //          (1) FanOut patterns greatly increase complexity (e.g. initialization order, sending strategies)
  //          (2) InfrastructureIn queues must cooperate with Art for freeze and snapshot semantics
  //          (3) AADL queue capacity and overflow strategies definable in "art world," not "infrastructure world"

  /**
   * Bootstraps the infrastructure of a particular out port.
   * This method occurs AFTER startInfrastructureIn() is called for all connected in ports. // todo synchronize this across networks
   *
   * @param portId the id of the port to bootstrap
   * @return a non-blocking Enqueue to send (or queue) outbound DataContent
   */
  def startInfrastructureOut(portId: Art.PortId): Enqueue[DataContent]
}

@sig trait InfrastructureInOut extends InfrastructureIn with InfrastructureOut {
  // no members
}

@datatype class InfrastructureCombiner(val in: InfrastructureIn, val out: InfrastructureOut) extends InfrastructureInOut with InfrastructureIn with InfrastructureOut {

  override def startInfrastructureIn(portId: Art.PortId, producer: Enqueue[DataContent]): Unit = {
    return in.startInfrastructureIn(portId, producer)
  }

  override def startInfrastructureOut(portId: Art.PortId): Enqueue[DataContent] = {
    return out.startInfrastructureOut(portId)
  }

  override def string: String = {
    return s"InfrastructureCombiner {\n  ${in.string},\n  ${out.string}\n}"
  }

}

/*
 * QUEUE APIS
 */

// dequeue has more methods. it's what infrastructure can wrap
@sig trait Dequeue[E] {
  def drain(consumer: E => Unit): Unit
  def drainWithLimit(consumer: E => Unit, limit: Z): Unit
  def isEmpty(): B // needed for dispatchStatus in ArtNative
  def peek(): Option[E]
}

@sig trait Enqueue[E] {
  def offer(e: E): B
}

@sig trait Queue[E] extends Dequeue[E] with Enqueue[E] {

}

@datatype class QueueFusion[E](enqueue: Enqueue[E], dequeue: Dequeue[E]) extends Queue[E] with Enqueue[E] with Dequeue[E] {

    override def peek(): Option[E] = {
      return dequeue.peek()
    }

    override def offer(e: E): B = {
      return enqueue.offer(e)
    }

    override def drain(consumer: E => Unit): Unit = {
      return dequeue.drain(consumer)
    }

    override def drainWithLimit(consumer: E => Unit, limit: Z): Unit = {
      return dequeue.drainWithLimit(consumer, limit)
    }

    override def isEmpty(): B = {
      return dequeue.isEmpty()
    }

    override def string: String = {
      return dequeue.string
    }
}

/*
 * DATA CLASSES USED TO MAP PORTS TO THEIR SERVICE IMPLEMENTATIONS
 */

@sig sealed trait PortServiceBundle {
 // has common top type instead of bottom type (used to specify direction to arch)
}

@datatype class InPortServiceBundle(infrastructure: InfrastructureIn,
                                    queue: () => Queue[DataContent]) extends PortServiceBundle {
}

/*
 * Q: Why does OutPortServiceBundle have a queue supplier if it
 */
@datatype class OutPortServiceBundle(infrastructure: InfrastructureOut,
                                     queue: () => Queue[DataContent]) extends PortServiceBundle {
}

/*
 * DATA CLASS + HELPER METHODS USED TO MAP PORTS TO THEIR INFRASTRUCTURE QUEUES AFTER LAUNCHING
 */

object InfrastructureRegistry {

  //    l""" ensures result ≡ (∃i: [0, s.size) s(i) ≡ e) """

  type PortTopic = art.Art.PortId


  // Checks if a connection "s" (value from Art.connections) contains out port "e"
  // @pure def seqContains[I, T](s: IS[I, T], e: T): B = {
  @pure def seqContains(s: IS[Art.ConnectionId, PortTopic], e: Art.PortId): B = {
    for (v <- s) {
      if (v == e) {
        return T
      }
    }
    return F
  }

  def portToTopic(portId: Art.PortId): PortTopic = {
    val port: UPort = Art.port(portId)
    if (port.mode == PortMode.EventOut || port.mode == PortMode.DataOut) {
      return portId
    } else if (port.mode == PortMode.EventIn || port.mode == PortMode.DataIn) {
      for (i <- Art.connections.indices) {
        if (seqContains(Art.connections(i), portId)) {
          return i
        }
      }
    }
    println("WARNING: portToTopic ERROR!")
    halt("impossible to convert port to topic (in SystemTopology.portToTopic)")
  }

  def launchInPort(portId: Art.PortId, bundle: InPortServiceBundle): Dequeue[DataContent] = {
    val topic: PortTopic = portToTopic(portId)
    // load services
    val infrastructure: InfrastructureIn = bundle.infrastructure
    val sharedQueue = bundle.queue()
    val enqueue: Enqueue[DataContent] = sharedQueue
    val dequeue: Dequeue[DataContent] = sharedQueue

    println(s"starting in port $portId topic $topic where ports are ${Art.ports}")
    infrastructure.startInfrastructureIn(portId, enqueue) // accepts the "enqueue" part of the queue

    return dequeue // was DeserializingDequeue(dequeue, deserializer) before moving serialization to infrastructure
  }

  def launchOutPort(portId: Art.PortId, bundle: OutPortServiceBundle): Enqueue[DataContent] = {
    val topic: PortTopic = portToTopic(portId)
    // load services
    val infrastructure: InfrastructureOut = bundle.infrastructure
    // todo queue will be used again once (if) we add "pull" data strategy support
//    val sharedQueue: Queue[DataContent] = bundle.queue()
//    val enqueue: Enqueue[DataContent] = sharedQueue
//    val dequeue: Dequeue[DataContent] = sharedQueue

    // has dequeue section outside of boundary, enqueue section within

    println(s"starting out port $portId topic $topic where ports are ${Art.ports}")

    // goal: make the enqueue trigger the dequeue drain when elements are placed inside
    // we have a way to put stuff in the queue, but nothing to trigger the drain that pushes it
    val wrapper: Enqueue[DataContent] = infrastructure.startInfrastructureOut(portId)
//    val combiner: Enqueue[DataContent] = new QueueFusion[DataContent](enqueue, wrapper)
    return wrapper

//    return enqueue // was SerializingEnqueue(enqueue, serializer) before moving deserialization to infrastructure
  }

  // launch and configure all port infrastructure, then return a registry pointing to the
  // resulting thread-safe queues which allow Art to access the infrastructure in Slang
  def launch(serviceBundles: IS[Art.PortId, Option[PortServiceBundle]]): InfrastructureRegistry = {
    val indices = serviceBundles.indices

    // launched
    val inboundPortQueues: ISZ[Option[Dequeue[DataContent]]] = indices.map((portId: Art.PortId) => {
      serviceBundles(portId) match {
        case Some(bundle) => {
          bundle match {
            case (in: InPortServiceBundle) => Some(launchInPort(portId, in)) // MUST BE FIRST CASE
            case (_: OutPortServiceBundle) => None()
          }
        }
        case None() => None()
      }
    })
    val outboundPortQueues: ISZ[Option[Enqueue[DataContent]]] = indices.map((portId: Art.PortId) => {
      serviceBundles(portId) match {
        case Some(bundle) => {
          bundle match {
            case (out: OutPortServiceBundle) => Some(launchOutPort(portId, out)) // MUST BE FIRST CASE
            case (_: InPortServiceBundle) => None()
          }
        }
        case None() => None()
      }
    })
    return InfrastructureRegistry(inboundPortQueues, outboundPortQueues)
  }
}

@datatype class InfrastructureRegistry(inboundPortQueues: ISZ[Option[Dequeue[DataContent]]],
                                       outboundPortQueues: ISZ[Option[Enqueue[DataContent]]]) {

  @pure def seqContains(s: IS[Art.ConnectionId, Art.PortId], e: Art.PortId): B = {
    for (v <- s) {
      if (v == e) {
        return T
      }
    }
    return F
  }

    def contains(portId: Art.PortId): B = {
      val pid: Z = portId.toZ
      return ISZOps(inboundPortQueues.indices).contains(pid) || ISZOps(outboundPortQueues.indices).contains(pid)
    }
}
