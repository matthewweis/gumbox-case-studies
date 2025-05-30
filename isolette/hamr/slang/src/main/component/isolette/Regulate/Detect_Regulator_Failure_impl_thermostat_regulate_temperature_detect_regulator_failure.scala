// #Sireum

package isolette.Regulate

import org.sireum._
import isolette.Isolette_Data_Model.Failure_Flag_impl
import isolette._

// This file will not be overwritten so is safe to edit
object Detect_Regulator_Failure_impl_thermostat_regulate_temperature_detect_regulator_failure {

  def initialise(api: Detect_Regulator_Failure_impl_Initialization_Api): Unit = {
    api.put_internal_failure(Failure_Flag_impl(F))
  }

  def timeTriggered(api: Detect_Regulator_Failure_impl_Operational_Api): Unit = {  }

  def finalise(api: Detect_Regulator_Failure_impl_Operational_Api): Unit = { }
}
