// #Sireum

package tc

import org.sireum._
import art._
import tc.Arch._
import tc.DemoUtils._

// This file was auto-generated.  Do not edit

object TempSensorClientArch {

  val ad : ArchitectureDescription = {

    ArchitectureDescription(
      components = IS[Art.BridgeId, Bridge] (TempControlSoftwareSystem_p_Instance_tcproc_tempSensor),

      connections = IS[Art.ConnectionId, UConnection] (
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempSensor.currentTemp, to = TempControlSoftwareSystem_p_Instance_tcproc_tempControl.currentTemp),
        Connection(from = TempControlSoftwareSystem_p_Instance_tcproc_tempSensor.currentTemp, to = TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface.currentTemp)
      )
    )
  }

  val services: IS[art.Art.PortId, Option[PortServiceBundle]] = {
    val services: MS[art.Art.PortId, Option[PortServiceBundle]] = MS.create[art.Art.PortId, Option[PortServiceBundle]](Art.numPorts, None())

    // any connection coming FROM a bridge we "own" must have SENDING services.
    services(TempControlSoftwareSystem_p_Instance_tcproc_tempSensor.currentTemp.id) = Some(bundleOut(jms, dataQueue))

    services.toIS
  }

}
