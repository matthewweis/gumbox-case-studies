// #Sireum

package tc

import org.sireum._
import art._
import tc.Arch._
import tc.DemoUtils._

// This file was auto-generated.  Do not edit

object CoolingFan_and_TempControl_Arch {

  val ad : ArchitectureDescription = {

    ArchitectureDescription(
      components = IS[Art.BridgeId, Bridge] (TempControlSoftwareSystem_p_Instance_tcproc_fan, TempControlSoftwareSystem_p_Instance_tcproc_tempControl),

      connections = IS[Art.ConnectionId, UConnection] (
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempSensor.currentTemp, to = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.currentTemp),
//        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempSensor.currentTemp, to = TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface.currentTemp),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_fan.fanAck, to = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanAck),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanCmd, to = TempControlSoftwareSystem_p_Instance_tcproc_fan.fanCmd),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface.setPoint, to = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.setPoint)
      )
    )
  }

  val services: IS[art.Art.PortId, Option[PortServiceBundle]] = {
    val services: MS[art.Art.PortId, Option[PortServiceBundle]] = MS.create[art.Art.PortId, Option[PortServiceBundle]](Art.numPorts, None())

    // Notice connections between local ports use the "local" infrastructure implementation.
    // Using the "JMS" implementation is also acceptable (e.g. for monitoring, or demo purposes).

    // fan
    services(TempControlSoftwareSystem_p_Instance_tcproc_fan.fanAck.id) = Some(bundleOut(local, dataQueue))
    services(TempControlSoftwareSystem_p_Instance_tcproc_fan.fanCmd.id) = Some(bundleIn(local, dataQueue))

    // temp control
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanCmd.id) = Some(bundleOut(local, dataQueue))
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempControl.currentTemp.id) = Some(bundleIn(jms, dataQueue))
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanAck.id) = Some(bundleIn(local, dataQueue))
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempControl.setPoint.id) = Some(bundleIn(jms, dataQueue))

    services.toIS // return immutable map of: PortID => Option[PortServiceBundle]
  }

}
