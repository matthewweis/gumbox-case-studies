package art

import org.sireum._
import art.DispatchPropertyProtocol.{Periodic, Sporadic}
import art.scheduling.Scheduler
import org.sireum.S64._
import scala.collection.mutable.{Map => MMap}

object ArtMessage {
  val UNSET_TIME: Art.Time = s64"-1"
}

case class ArtMessage(data: DataContent,

                      var srcPortId: Option[Art.PortId] = None(),
                      var dstPortId: Option[Art.PortId] = None(),

                      // when putValue was called by producer
                      var putValueTimestamp: Art.Time = ArtMessage.UNSET_TIME,

                      // when sendOutput transferred message from out port var of producer
                      var sendOutputTimestamp: Art.Time = ArtMessage.UNSET_TIME,

                      // when message arrived via transport layer
                      var dstArrivalTimestamp: Art.Time = ArtMessage.UNSET_TIME,

                      // when receiveInput transferred message to in port vars of consumer
                      var receiveInputTimestamp: Art.Time = ArtMessage.UNSET_TIME
                     )

object ArtNative_Ext {
  val noTime: Art.Time = s64"0"

  val slowdown: Z = 1

  //================================================================
  //   A r c h i t e c t u r e     D e s c r i p t i o n
  //================================================================

  // Architecture description includes any data structures built from Arch information
  // to support system execution (i.e., by making certain types of lookup of Arch
  // information easier).   This information persists across runs, i.e., it doesn't
  // need to be changed between different runs of the system as long as the architecture
  // has not changed.

  // JH: Refactored - moved out of legacy run method to enable separate
  // init/compute/finalize phase methods.
  //    This structure is essentially a helper for accessing the Arch description.
  //    We should study the Arch description to assess (more systematically)
  //    what types of helpers are needed and where they would go.
  /*
  var activeBridges: IS[Art.BridgeId, Art.BridgeId] = ISZ()
  def setUpArchitecture() : Unit = {
    for(e <- Art.bridges.elements if(e.nonEmpty)) {
      activeBridges = activeBridges :+ e.get.id
    }
  }
  def tearDownArchitecture() : Unit = {
    activeBridges = IS[Art.BridgeId, Art.BridgeId]()
  }
  */

  //================================================================
  //   P l a t f o r m     S t a t e
  //================================================================

  // Architecture description includes any infrastructure necessary to
  // support the platform including communication instrastructure and
  // other resources that may exist across multiple executions

  /*
  def setUpPlatform() : Unit = {
  }
  def tearDownPlatform() : Unit = {
  }
  */

  //================================================================
  //   S y s t e m     S t a t e
  //================================================================

  // todo infrastructure code used to hold DataContent, looks like we want ArtMessage now? Added new QueueConcMap wrapper
  val inInfrastructurePorts: MMap[Z, Dequeue[ArtMessage]] = concMap()
  val outInfrastructurePorts: MMap[Z, Enqueue[ArtMessage]] = concMap()
  val inPortVariables: MMap[Z, Queue[ArtMessage]] = concMap()
  val outPortVariables: MMap[Z, Queue[ArtMessage]] = concMap()


  // Initializes system state in preparation for execution of initialize, compute, and finalize phases
  // System state includes any state associated with system execution, e.g., things that would need to be
  // set up and cleared between runs, but does not include things related to system architecture or platform
  // infrastructure that could persist between runs.

//  def setUpSystemState(registry: InfrastructureRegistry): Unit = {
  def setUpSystemState(): Unit = {
    inInfrastructurePorts.clear()
    inPortVariables.clear()
    outPortVariables.clear()
    outInfrastructurePorts.clear()

    // cancel pending ArtTimer callbacks (also done after a test completes)
    ArtTimer_Ext.scheduledCallbacks.keys.foreach(ArtTimer_Ext.cancel)

    // setup queues for infrastructurePorts and portVariables
//    setUpPortQueues(registry)

    //scheduler.initialize()
  }

//  def setUpPortQueues(registry: InfrastructureRegistry): Unit = {
//    setUpPortVariableQueues()
//    setUpPortInfrastructureQueues()
//  }

  def initPortVariable(port: UPort): Unit = {
    // TODO -- where should this eventually be configured? Should probably always be same type as matching infrastructure?
    port.mode match {
      case PortMode.EventIn => inPortVariables(port.id.toZ) = Queues.createSingletonEventQueue()
      case PortMode.EventOut => outPortVariables(port.id.toZ) = Queues.createSingletonEventQueue()
      case PortMode.DataIn => inPortVariables(port.id.toZ) = Queues.createSingletonDataQueue()
      case PortMode.DataOut => outPortVariables(port.id.toZ) = Queues.createSingletonDataQueue()
    }
  }

  def setUpPortVariableQueues(): Unit = {
    Art.ports.foreach((maybePort: Option[art.UPort]) => {
      maybePort match {
        case Some(port) => initPortVariable(port)
        case None() => //logError("ArtNative", "WARNING: A port was empty after Art init.")
      }
    })
  }

  def setUpPortInfrastructureQueues(): Unit = {
    // todo let Dr. Hatcliff + Jason know isolated bridge tests still need access to services/ad for Queues to connect
    //      and ask them how they prefer I go about handling that. Nothing external to the bridge is launched, but the
    //      information in Arch.ad/services are needed for infrastructure ports to communicate.
    val allServices: IS[Art.PortId, Option[PortServiceBundle]] = tc.CompletelyLocalArch.services // todo pass this in from test?
    val ad: ArchitectureDescription = tc.CompletelyLocalArch.ad // todo pass this in from test?

    // list of services backing infrastructure in/out
    val services: IS[Art.PortId, Option[PortServiceBundle]] = {
      val services = MS.create[Art.PortId, Option[PortServiceBundle]](allServices.size, None[PortServiceBundle]())
      for (pid <- allServices.indices if Art.ports(pid).nonEmpty) {
        services(pid) = allServices(pid)
      }
      services.toIS
    }
    val registry: InfrastructureRegistry = InfrastructureRegistry.launch(services, ad) // sets up connections
    // convert registry ISZs to MMaps
    for (inPortId <- registry.inboundPortQueues.indices) {
      registry.inboundPortQueues(inPortId) match {
        case Some(dequeue) =>
          inInfrastructurePorts(inPortId) = new Queues_Ext.InfrastructureInPortQueueWrapper(Art.PortId.fromZ(inPortId), dequeue)
        case _ =>
      }
    }
    for (outPortId <- registry.outboundPortQueues.indices) {
      registry.outboundPortQueues(outPortId) match {
        case Some(enqueue) =>
          outInfrastructurePorts(outPortId) = new Queues_Ext.InfrastructureOutPortQueueWrapper(Art.PortId.fromZ(outPortId), enqueue)
        case _ =>
      }
    }
  }

  def tearDownSystemState(): Unit = {
    inInfrastructurePorts.clear()
    inPortVariables.clear()
    outPortVariables.clear()
    outInfrastructurePorts.clear()

    setUpPortVariableQueues()
    setUpPortInfrastructureQueues()

    // cancel pending ArtTimer callbacks (also done after a test completes)
    ArtTimer_Ext.scheduledCallbacks.keys.foreach(ArtTimer_Ext.cancel)
  }


  //===============================================================================
  //  Port-related AADL run-time services
  //===============================================================================

  // JH: Refactored -- renamed port data structures
  // TODO -- Consider whether changing the value from ArtMessage to Art.DataContent should happen here (instead of in getValue)
  def receiveInput(eventPortIds: ISZ[Art.PortId], dataPortIds: ISZ[Art.PortId]): Unit = {
    // remove any old events from previous dispatch
    for (portId <- eventPortIds if inPortVariables.contains(portId.toZ)) {
      inPortVariables(portId.toZ).offer(null)
    }

    // transfer received data/events from the infrastructure ports to the port variables
    for (portId <- eventPortIds) {
      inInfrastructurePorts.get(portId.toZ) match {
        case scala.Some(queueConsumer: Dequeue[ArtMessage]) => {
          // todo discuss semantics for non-single queues (do we want to drain like this?)
          queueConsumer.drain((msg: ArtMessage) => {
            println(s"Copying from infrastructure to event port=$portId, msg=$msg")
            inPortVariables(portId.toZ).offer(msg)
          })
        }
        case _ =>
      }
    }
    for (portId <- dataPortIds) {
      inInfrastructurePorts.get(portId.toZ) match {
        case scala.Some(queueConsumer: Dequeue[ArtMessage]) => {
          // todo discuss semantics for non-single queues (do we want to drain like this?)
          queueConsumer.drain((msg: ArtMessage) => {
            println(s"Copying data from infrastructure to data port=$portId, msg=$msg")
            inPortVariables(portId.toZ).offer(msg)
          })
        }
        case _ =>
      }
    }
  }

  def putValue(portId: Art.PortId, data: DataContent): Unit = {
    // wrap the Art.DataContent value into an ArtMessage with time stamps
    outPortVariables(portId.toZ).offer(ArtMessage(data = data, srcPortId = Some(portId), putValueTimestamp = Art.time()))
  }

  def getValue(portId: Art.PortId): Option[DataContent] = {
    // To return the value of the port to the application code, project
    // out the actual payload value (v.data) from ArtMessage (which includes timestamps, etc.)
    // to Art.DataContent (the "top"/union data type supported by Art.
    // The projecting preserves the option of structure of ArtMessage value.
    return inPortVariables.get(portId.toZ) match {
      case scala.Some(queue: Queue[ArtMessage]) => queue.peek().map((msg: ArtMessage) => msg.data)
      case _ => org.sireum.None[DataContent]()
    }
  }

  // JH: Refactored
  //      - change names of port data structures
  //      - introduce a distinction between output port variables and output infrastructure ports
  // ToDo: Introduce the concept of a distinct transfer method.
  //  The way that implementation treats outPortVariables and outInfrastructurePorts is almost nonsensical
  //  until that refactoring is made.
  def sendOutput(eventPortIds: ISZ[Art.PortId], dataPortIds: ISZ[Art.PortId]): Unit = { // SEND_OUTPUT
    for (srcPortId <- eventPortIds ++ dataPortIds) {
      outPortVariables.get(srcPortId.toZ) match {
        case scala.Some(queueConsumer: Queue[ArtMessage]) =>
          // todo send on emission as well as receive

          outInfrastructurePorts.get(srcPortId.toZ) match {
            case scala.Some(queueProducer: Enqueue[ArtMessage]) => queueConsumer.drain((msg: ArtMessage) => queueProducer.offer(msg))
            case _ => //println(s"WARNING: no infrastructure out defined for port $srcPortId")
          }
        case _ =>
      }

      // simulate sending msg via transport middleware
      // todo no longer need to simulate transport. A custom debug infrastructure can be used if we want this feature?

      // payload delivered so remove it from out infrastructure port
      // todo No longer need to manually remove from out infrastructure port.
      //      Instead a port's Infrastructure or Queue policy will be honored.
      //      A custom debug infrastructure can be used if this feature is desired?
    }
  }

  // JH: Refactor
  // Manually added by JH to support debugging framework
  //  -- to support being able to see inputs and outputs of a a thread (before/after compute),
  //     clearing of output ports is removed from send_output.
  //  This function is called by scheduler, before calling compute to initialize the
  //  component port state
//  def clearPortVariables(bridgeId: Art.BridgeId): Unit = {
//    assert(F, "TODO")
//    // val b = Art.bridge(bridgeId) -- refactor
//    // ToDo: the computation of input/output port ids should be helper functions in Bridge
//    // compute inPortIds
//    val inPortIds = Art.bridges(bridgeId.toZ).get.ports.eventIns.elements.map(_.id) ++ Art.bridges(bridgeId.toZ).get.ports.dataIns.elements.map(_.id)
//    // iterate through inPortIds and clear the value of each corresponding port variable
//    for (portId <- inPortIds) {
//      inPortVariables -= portId.toZ;
//      initPortVariable(Art.port(portId))
//    }
//    // compute outPortIds
//    val outPortIds = Art.bridges(bridgeId.toZ).get.ports.eventOuts.elements.map(_.id) ++ Art.bridges(bridgeId.toZ).get.ports.dataOuts.elements.map(_.id)
//    // iterate through outPortIds and clear the value of each corresponding port variable
//    for (portId <- outPortIds) {
//      outPortVariables -= portId.toZ
//      initPortVariable(Art.port(portId))
//    }
//  }

  //===============================================================================
  //  HAMR Library Services
  //===============================================================================

  def logInfo(title: String, msg: String): Unit = log("info", title, msg)

  def logError(title: String, msg: String): Unit = log("error", title, msg)

  def logDebug(title: String, msg: String): Unit = log("debug", title, msg)

  def time(): Art.Time = toS64(System.currentTimeMillis())

  //===============================================================================
  //  AADL Thread/Scheduling services
  //===============================================================================

  // JH: Refactor to match logic in semantics, group with dispatch status
  def shouldDispatch(bridgeId: Art.BridgeId): B = {
    assert(Art.bridges(bridgeId.toZ).nonEmpty, s"Bridge ${bridgeId} does not exist")

    Art.bridges(bridgeId.toZ).get.dispatchProtocol match {
      case DispatchPropertyProtocol.Periodic(_) => return T
      case DispatchPropertyProtocol.Sporadic(minRate) =>
        return Art.bridges(bridgeId.toZ).get.ports.eventIns.elements.exists(
          port => inInfrastructurePorts.contains(port.id.toZ) && !inInfrastructurePorts(port.id.toZ).isEmpty())
    }
  }

  // JH: Refactored -- renamed port data structures
  //     ToDo: add comments justifying various sections of the logic by reference to standard clauses
  def dispatchStatus(bridgeId: Art.BridgeId): DispatchStatus = {
    val ret: DispatchStatus = Art.bridges(bridgeId.toZ).get.dispatchProtocol match {
      case Periodic(_) => TimeTriggered()
      case Sporadic(_) =>
        // get ids for non-empty input event ports
        val portIds = ISZ[Art.PortId](Art.bridge(bridgeId).ports.eventIns.elements.map(_.id).filter(it => inInfrastructurePorts.get(it.toZ) match {
          case scala.Some(consumer) => !consumer.isEmpty()
          case _ => F
        }): _*)
        val urgentFifo: Seq[Art.PortId] = portIds.map((pid: Art.PortId) => Art.port(pid)).elements.sortWith { // reverse sort
          // sorting function to make prioritized sequence of event port ids
          //   compare p1 to p2  (p1 represents the port to process earlier, i.e., should have priority)
          case (p1: UrgentPort[_], p2: UrgentPort[_]) => Z
            // if p1 has a strictly less urgency it comes after p2
            if (p1.urgency < p2.urgency) F
            // if p1 has a strictly greater urgency, it comes before p2
            else if (p1.urgency > p2.urgency) T
            // if p1 and p2 have the same urgency, the ordering is determined by arrival timestamps
            else inInfrastructurePorts(p1.id.toZ).peek().map((msg: ArtMessage) => msg.dstArrivalTimestamp).getOrElseEager(S64.Max) < inInfrastructurePorts(p2.id.toZ).peek().map((msg: ArtMessage) => msg.dstArrivalTimestamp).getOrElseEager(S64.Max)
          case (_: UrgentPort[_], _: Port[_]) => T // urgent ports take precedence
          case (_: Port[_], _: UrgentPort[_]) => F // urgent ports take precedence
          case (p1: Port[_], p2: Port[_]) =>
            inInfrastructurePorts(p1.id.toZ).peek().map((msg: ArtMessage) => msg.dstArrivalTimestamp).getOrElseEager(S64.Max) < inInfrastructurePorts(p2.id.toZ).peek().map((msg: ArtMessage) => msg.dstArrivalTimestamp).getOrElseEager(S64.Max)
        }.map(_.id)
        EventTriggered(ISZ[Art.PortId](urgentFifo: _*))
    }
    return ret
  }

  //===============================================================================
  //  AADL Execution Phases
  //
  //   Note: this could be synchronized a bit more with thread states / hybrid automata
  //   in AADL standard
  //===============================================================================

  def initializePhase(): Unit = {
    logInfo(Art.logTitle, s"Initializing component...")
  }

  def computePhase(): Unit = {
    logInfo(Art.logTitle, s"Begin execution...")
  }

  def finalizePhase(): Unit = {
    logInfo(Art.logTitle, s"End execution...")

    ArtTimer_Ext.finalise()
  }

  var logStream: java.io.PrintStream = System.out

  def log(kind: String, title: String, msg: String): Unit = {
    logStream.println(st"""{ "log" : "$kind", "title" : ${Json.Printer.printString(title)}, "msg" : ${Json.Printer.printString(msg)}, "time" : "${time()}" }""".render)
    logStream.flush()
  }

  def toS64(value: Long): S64 = S64(value)

  def concMap[K, V](): MMap[K, V] = {
    import org.sireum.$internal.CollectionCompat.Converters._
    new java.util.concurrent.ConcurrentHashMap[K, V].asInstanceOf[java.util.Map[K, V]].asScala
  }




  /////////////
  // TESTING //
  /////////////

  /**
   * Sets up the state of a thread component (identified by bridge) for the purpose of
   * testing.
   *
   * An analogue to this method does not show up in developer-written unit tests because
   * it's invoked behind the scenes by the automatically generated unit test infrastructure
   * as a prelude to each test.
   */
  def initTest(bridge: Bridge): Unit = {
    // delete ALL port values
    inInfrastructurePorts.clear()
    inPortVariables.clear()
    outPortVariables.clear()
    outInfrastructurePorts.clear()

    setUpPortVariableQueues()
    setUpPortInfrastructureQueues()

    // cancel pending ArtTimer callbacks (also done after a test completes)
    ArtTimer_Ext.scheduledCallbacks.keys.foreach(ArtTimer_Ext.cancel)

    bridge.entryPoints.testInitialise()
    logInfo(Art.logTitle, s"Initialized bridge: ${bridge.name}")
  }

  /**
   * Executes the application code in the Initialize Entry Point for the component (identified
   * by given bridge) for the purposes of testing.  This is achieved by
   * calling the testInitialise() method on given bridge.
   *
   * Precondition: initTest() has been called prior.
   *
   * Unlike [[Art.run()]], this method does NOT wrap compute calls in a try-catch block.
   * This is to ensure no exceptions are overlooked during testing.
   */
  def testInitialise(bridge: Bridge): Unit = {
    bridge.entryPoints.testInitialise()
  }

  /**
   * Executes the application code in the Compute Entry Point for the component (identified
   * by given bridge) for the purposes of testing.  This is achieved by
   * calling the testCompute() method on given bridge.
   *
   * Precondition: initTest() has been called prior.
   *
   * Unlike [[Art.run()]], this method does NOT wrap compute calls in a try-catch block.
   * This is to ensure no exceptions are overlooked during testing.
   */
  def testCompute(bridge: Bridge): Unit = {
    bridge.entryPoints.testCompute()
  }

  /**
   * Calls the finalize entry points on all registered bridges.
   * Testers should NOT call this method because BridgeTestSuite will automatically call this method after each test.
   *
   */
  def finalizeTest(bridge: Bridge): Unit = {
    bridge.entryPoints.finalise()
    logInfo(Art.logTitle, s"Finalized bridge: ${bridge.name}")

    // cancel pending ArtTimer callbacks (also done before a test begins)
    ArtTimer_Ext.scheduledCallbacks.keys.foreach(ArtTimer_Ext.cancel)
  }

  // JH: Refactored
  //   add system test capability
//  def initSystemTest(services: IS[Art.PortId, Option[PortServiceBundle]], scheduler: Scheduler): Unit = {
  def initSystemTest(scheduler: Scheduler): Unit = {
    Art.setUpArchitecture()
    Art.setUpPlatform()
//    Art.setUpSystemState(services, scheduler)
    Art.setUpSystemState(scheduler)
    logInfo(Art.logTitle, s"Initialized system for system test")
  }

  //  def executeSystemTest(): Unit = $

  // JH: Refactored
  //   add system test capability
  def finalizeSystemTest(): Unit = {
    Art.tearDownSystemState()
    Art.tearDownPlatform()
    Art.tearDownArchitecture()
  }

  // JH: Refactor
  //  Add code to address the fact that out port variables are now distinct from
  //  out infrastructure ports,  i.e., we must copy from out port variables to
  //  out infrastructure ports
  /**
   * A method that replaces bridge.compute()'s calls to [[Art.sendOutput()]] in
   * its equivalent testCompute() method.
   *
   * This method is currently a NO-OP, but may gain functionality later.
   *
   * @param eventPortIds the event ports to be "copied and cleared" (but currently nothing happens)
   * @param dataPortIds the data ports to be "copied and cleared" (but currently nothing happens)
   */
  def releaseOutput(eventPortIds: ISZ[Art.PortId], dataPortIds: ISZ[Art.PortId]): Unit = { // testing SEND_OUTPUT
    // note: sendOutput is usually accessed via:
    //   Art.sendOutput -> ArtNative.sendOutput -> ArtNative_Ext.sendOutput
    //JH added:
    for (srcPortId <- eventPortIds ++ dataPortIds) {
      outPortVariables.get(srcPortId.toZ) match {
        case scala.Some(variableQueue: Queue[ArtMessage]) =>
          outInfrastructurePorts.get(srcPortId.toZ) match {
            case scala.Some(infrastructureEnqueue: Enqueue[ArtMessage]) => {
              variableQueue.drain((msg: ArtMessage) => infrastructureEnqueue.offer(msg))
            }
            case _ => require(F, "Unable to release output.")
          }
        // outInfrastructurePorts(srcPortId.toZ) = outPortVariables(srcPortId.toZ)
        case _ => require(F, "Infeasible")
      }
    }
  }

  /**
   * Because a bridge's testCompute() doesn't clear outputs, this method can be
   * used by users to manually clear the output if desired. This is useful for
   * tests involving multiple dispatches.
   */
  def manuallyClearOutput(): Unit = {
    outPortVariables.clear()
    Art.ports.foreach((maybePort: Option[art.UPort]) => {
      maybePort match {
        case Some(port) => {
          if (port.mode == PortMode.DataOut || port.mode == PortMode.EventOut) {
            initPortVariable(port)
          }
        }
        case None() => logError("ArtNative", "WARNING: A port was empty after Art init.")
      }
    })
  }

  // JH: Refactor
  // ToDo: Rename the functions below to align with the variable names inInfrastructurePort, etc.
  /**
   * Inserts a value into an "infrastructure in" port. For testing only, normally
   * this is handled by Art.
   *
   * @param dstPortId the portId to place the passed [[DataContent]] into
   * @param data the [[DataContent]] which will be placed in the dstPort
   */
  def insertInInfrastructurePort(dstPortId: Art.PortId, data: DataContent): Unit = {
    // note: that could would be changed when we refactor to support event queues of size > 1
    val artMessage = ArtMessage(data = data, dstPortId = Some(dstPortId), dstArrivalTimestamp = Art.time())
    // note: right now, there is no difference in the logic between data and event ports, but keep the
    // logic separate for future refactoring
    Art.port(dstPortId).mode match {
      case PortMode.DataIn | PortMode.DataOut =>
        inInfrastructurePorts(dstPortId.toZ).asInstanceOf[Queue[ArtMessage]].offer(artMessage)
      case PortMode.EventIn | PortMode.EventOut =>
        inInfrastructurePorts(dstPortId.toZ).asInstanceOf[Queue[ArtMessage]].offer(artMessage)
    }
  }


  /**
   * Returns the value of an in infrastructure port.
   *
   * @param portId the id of the INPUT port to return a value from
   * @return If the port is non-empty, a [[Some]] of [[DataContent]]. Otherwise [[None]].
   */
  def observeInInfrastructurePort(portId: Art.PortId): Option[DataContent] = {
    // right now, with event data port queues limited to size one, there is no difference in the logic
    // between how data ports are treated, and how event/event data ports are treated.
    Art.port(portId).mode match {
      case PortMode.DataIn =>
        inInfrastructurePorts.get(portId.toZ) match {
          case scala.Some(dequeue: Dequeue[ArtMessage]) => dequeue.peek() match {
            case org.sireum.Some(value: ArtMessage) => org.sireum.Some[DataContent](value.data)
            case org.sireum.None() => org.sireum.None[DataContent]()
          }
          case scala.None => org.sireum.None[DataContent]()
        }
      case PortMode.EventIn =>
        inInfrastructurePorts.get(portId.toZ) match {
          case scala.Some(dequeue: Dequeue[ArtMessage]) => dequeue.peek() match {
            case org.sireum.Some(value: ArtMessage) => org.sireum.Some[DataContent](value.data)
            case org.sireum.None() => org.sireum.None[DataContent]()
          }
          case scala.None => org.sireum.None[DataContent]()
        }
      case _ => {
        assert(false, "expecting in port")
        org.sireum.None[DataContent]()
      }
    }
  }

  /**
   * Returns the value of an infrastructure out port.
   *
   * @param portId the id of the OUTPUT port to return a value from
   * @return If the port is non-empty, a [[Some]] of [[DataContent]]. Otherwise [[None]].
   */
  def observeOutInfrastructurePort(portId: Art.PortId): Option[DataContent] = {
    // note: would be changed when we refactor to support event queues of size > 1
    outInfrastructurePorts.get(portId.toZ) match {
      case scala.Some(enqueue: Enqueue[ArtMessage]) => enqueue match {
        // case 1: infrastructureOut's implementation supports Read+Write
        case queue: Queue[ArtMessage] => queue.peek() match {
          case org.sireum.Some(value: ArtMessage) => org.sireum.Some[DataContent](value.data)
          case org.sireum.None() => org.sireum.None[DataContent]()
        }
        // case 2: infrastructureOut's implementation supports R+W but is not backed by type-erasure friendly Queue
        case dequeue: Dequeue[_] => dequeue.peek() match {
          case org.sireum.Some(value: ArtMessage) => {
            // while 'T extends Enqueue with Dequeue' is technically valid for custom queues, 'T extends Queue' is
            // better because it prevent type erasure when pattern matching
            eprintln("WARNING: Custom infrastructure queues supporting Read+Write should also extend the Queue trait")
            org.sireum.Some[DataContent](value.data)
          }
          // impossible state included or type checker
          case org.sireum.Some(unmatchable) => {
            assert(false, st"reached invalid state as outInfrastructure's queue should contain ArtMessage: ${unmatchable}".render)
            org.sireum.None[DataContent]()
          }
          case org.sireum.None() => org.sireum.None[DataContent]()
        }
        case _ => {
          assert(false, "it is impossible to observe an outInfrastructure port if its underlying implementation doesn't support reads")
          org.sireum.None[DataContent]()
        }
      }
      case scala.None => org.sireum.None[DataContent]()
    }
  }

  /**
   * Returns the value of an application in port.
   *
   * @param portId the id of the INPUT port to return a value from
   * @return If the port is non-empty, a [[Some]] of [[DataContent]]. Otherwise [[None]].
   */
  def observeInPortVariable(portId: Art.PortId): Option[DataContent] = {
    // right now, with event data port queues limited to size one, there is no difference in the logic
    // between how data ports are treated, and how event/event data ports are treated.
    Art.port(portId).mode match {
      case PortMode.DataIn =>
        inPortVariables.get(portId.toZ) match {
          case scala.Some(dequeue: Dequeue[ArtMessage]) => dequeue.peek() match {
            case org.sireum.Some(value: ArtMessage) => org.sireum.Some[DataContent](value.data)
            case org.sireum.None() => org.sireum.None[DataContent]()
          }
          case scala.None => org.sireum.None[DataContent]()
        }
      case PortMode.EventIn =>
        inPortVariables.get(portId.toZ) match {
          case scala.Some(dequeue: Dequeue[ArtMessage]) => dequeue.peek() match {
            case org.sireum.Some(value: ArtMessage) => org.sireum.Some[DataContent](value.data)
            case org.sireum.None() => org.sireum.None[DataContent]()
          }
          case scala.None => org.sireum.None[DataContent]()
        }
      case _ => {
        assert(false, "expecting in port")
        org.sireum.None[DataContent]()
      }
    }
  }

  /**
     * Returns the value of an application out port.
     *
     * @param portId the id of the OUTPUT port to return a value from
     * @return If the port is non-empty, a [[Some]] of [[DataContent]]. Otherwise [[None]].
     */
  def observeOutPortVariable(portId: Art.PortId): Option[DataContent] = {
    // note: that could would be changed when we refactor to support event queues of size > 1
    outPortVariables.get(portId.toZ) match {
      case scala.Some(dequeue: Dequeue[ArtMessage]) => dequeue.peek() match {
        case org.sireum.Some(value: ArtMessage) => org.sireum.Some[DataContent](value.data)
        case org.sireum.None() => org.sireum.None[DataContent]()
      }
      case scala.None => org.sireum.None[DataContent]()
    }
  }
}