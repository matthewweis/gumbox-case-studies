// #Sireum

package tc

import org.sireum._
import art._
import tc.Arch._
import tc.DemoUtils._

// This file was auto-generated.  Do not edit

object DemoJmsArch {

  val ad : ArchitectureDescription = {

    ArchitectureDescription(
      components = IS[Art.BridgeId, Bridge] (TempControlSoftwareSystem_p_Instance_tcproc_tempSensor, TempControlSoftwareSystem_p_Instance_tcproc_fan, TempControlSoftwareSystem_p_Instance_tcproc_tempControl, TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface),

      connections = IS[Art.ConnectionId, UConnection] (
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempSensor.currentTemp, to = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.currentTemp),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempSensor.currentTemp, to = TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface.currentTemp),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_fan.fanAck, to = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanAck),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanCmd, to = TempControlSoftwareSystem_p_Instance_tcproc_fan.fanCmd),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface.setPoint, to = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.setPoint)
      )
    )
  }

  val services: IS[art.Art.PortId, Option[PortServiceBundle]] = {
    val services: MS[art.Art.PortId, Option[PortServiceBundle]] = MS.create[art.Art.PortId, Option[PortServiceBundle]](Art.numPorts, None())

    // fan
    services(TempControlSoftwareSystem_p_Instance_tcproc_fan.fanAck.id) = Some(bundleOut(jms, dataQueue))
    services(TempControlSoftwareSystem_p_Instance_tcproc_fan.fanCmd.id) = Some(bundleIn(jms, dataQueue))

    // temp sensor
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempSensor.currentTemp.id) = Some(bundleOut(jms, dataQueue))


    // temp control system
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanCmd.id) = Some(bundleOut(jms, dataQueue))
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempControl.currentTemp.id) = Some(bundleIn(jms, dataQueue))

    services(TempControlSoftwareSystem_p_Instance_tcproc_tempControl.fanAck.id) = Some(bundleIn(jms, dataQueue))
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempControl.setPoint.id) = Some(bundleIn(jms, dataQueue))

    // operator interface
    services(TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface.setPoint.id) = Some(bundleOut(jms, dataQueue))
    services(TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface.currentTemp.id) = Some(bundleIn(jms, dataQueue))


    services.toIS
  }

}
