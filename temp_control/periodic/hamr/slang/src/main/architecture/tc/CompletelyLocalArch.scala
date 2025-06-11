// #Sireum

package tc

import org.sireum._
import art._
import tc.Arch._

// This file was auto-generated.  Do not edit

object CompletelyLocalArch {

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

    val dataQueue: () => Queue[DataContent] = () => Queues.createSingletonDataQueue()
    val localIn: () => InfrastructureIn = () => Infrastructures.localIn(ad)
    val localOut: () => InfrastructureOut = () => Infrastructures.localOut(ad)

    val fan = TempControlSoftwareSystem_p_Instance_tcproc_fan
    val tempSensor = TempControlSoftwareSystem_p_Instance_tcproc_tempSensor
    val tempControl = TempControlSoftwareSystem_p_Instance_tcproc_tempControl
    val operatorInterface = TempControlSoftwareSystem_p_Instance_tcproc_operatorInterface

    services(fan.fanAck.id)                    = Some(OutPortServiceBundle (localOut(), dataQueue))
    services(fan.fanCmd.id)                    = Some(InPortServiceBundle  (localIn(),  dataQueue))
    services(tempSensor.currentTemp.id)        = Some(OutPortServiceBundle (localOut(), dataQueue))
    services(tempControl.fanCmd.id)            = Some(OutPortServiceBundle (localOut(), dataQueue))
    services(tempControl.currentTemp.id)       = Some(InPortServiceBundle  (localIn(),  dataQueue))
    services(tempControl.fanAck.id)            = Some(InPortServiceBundle  (localIn(),  dataQueue))
    services(tempControl.setPoint.id)          = Some(InPortServiceBundle  (localIn(),  dataQueue))
    services(operatorInterface.setPoint.id)    = Some(OutPortServiceBundle (localOut(), dataQueue))
    services(operatorInterface.currentTemp.id) = Some(InPortServiceBundle  (localIn(),  dataQueue))

    services.toIS
  }

}
