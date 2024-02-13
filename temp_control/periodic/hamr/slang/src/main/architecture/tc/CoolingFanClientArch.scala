// #Sireum

package tc

import org.sireum._
import art._
import tc.Arch._
import tc.DemoUtils._

// This file was auto-generated.  Do not edit

object CoolingFanClientArch {

  val ad : ArchitectureDescription = {

    ArchitectureDescription(
      components = IS[Art.BridgeId, Bridge] (TempControlSoftwareSystem_p_Instance_tcproc_fan),

      connections = IS[Art.ConnectionId, UConnection] (
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_fan.fanAck, to = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanAck),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanCmd, to = TempControlSoftwareSystem_p_Instance_tcproc_fan.fanCmd))
    )
  }

  val services: IS[art.Art.PortId, Option[PortServiceBundle]] = {
    val services: MS[art.Art.PortId, Option[PortServiceBundle]] = MS.create[art.Art.PortId, Option[PortServiceBundle]](Art.numPorts, None())

    // any connection coming FROM a bridge we "own" must have SENDING services
    services(TempControlSoftwareSystem_p_Instance_tcproc_fan.fanAck.id) = Some(bundleOut(jms, dataQueue))

    // any connection going TO a bridge we "own" must have port LISTENING services
    services(TempControlSoftwareSystem_p_Instance_tcproc_fan.fanCmd.id) = Some(bundleIn(jms, dataQueue))

    services.toIS // return immutable map of: PortID => Option[PortServiceBundle]
  }

}
