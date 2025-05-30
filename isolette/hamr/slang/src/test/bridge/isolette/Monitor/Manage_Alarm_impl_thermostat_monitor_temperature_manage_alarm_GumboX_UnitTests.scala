package isolette.Monitor

import isolette.{Config_F32, RandomLib}
import org.sireum._
import isolette.GumboXUtil.GumboXResult
import isolette.util.{Container, MutableBase, Profile, UnitTestConfigurationBatch}
import isolette.Monitor.Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_UnitTestConfiguration_Util._

// This file will not be overwritten so is safe to edit

class Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_GumboX_UnitTests extends Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_GumboX_TestHarness_ScalaTest {

  val verbose: B = F
  val failOnUnsatPreconditions: B = F

  val leftPartitionConfig = defaultComputewLConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions,
    name = "Compute_Config_Left_Partition_Config",
    description = "Generate a current_temp s.t. lower_alarm_temp <= current_temp <= lower_alarm_temp + 0.5",
    profile = new Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_Profile_PS_Trait with MutableBase {
      override def next: Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_PreState_Container_PS = {
        val low = api_lower_alarm_temp.nextIsolette_Data_ModelTemp_impl()
        val currentTemp = api_current_tempWstatus.set_Config_F32(
            api_current_tempWstatus.get_Config_F32(low = Some(low.value), high = Some(low.value + 0.5f)))
          .nextIsolette_Data_ModelTempWstatus_impl()
        return (Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_PreState_Container_PS(
          api_lower_alarm_temp = low,
          api_upper_alarm_temp = api_upper_alarm_temp.nextIsolette_Data_ModelTemp_impl(),
          api_current_tempWstatus = currentTemp,
          api_monitor_mode = api_monitor_mode.nextIsolette_Data_ModelMonitor_ModeType(),
          In_lastCmd = In_lastCmd.nextIsolette_Data_ModelOn_OffType()))
      }
      override def In_lastCmd: RandomLib = freshRandomLib
      override def api_lower_alarm_temp: RandomLib = freshRandomLib
      override def api_upper_alarm_temp: RandomLib = freshRandomLib
      override def api_current_tempWstatus: RandomLib = freshRandomLib
      override def api_monitor_mode: RandomLib = freshRandomLib
    })

  val rightPartitionConfig = defaultComputewLConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions,
    name = "Compute_Config_Right_Partition_Config",
    description = "Generate a current_temp s.t. upper_alarm_temp - 0.5 <= current_temp <= upper_alarm_temp",
    profile = new Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_Profile_PS_Trait with MutableBase {
      override def next: Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_PreState_Container_PS = {
        val high = api_upper_alarm_temp.nextIsolette_Data_ModelTemp_impl()
        val currentTemp = api_current_tempWstatus.set_Config_F32(
            api_current_tempWstatus.get_Config_F32(low = Some(high.value - 0.5f), high = Some(high.value)))
          .nextIsolette_Data_ModelTempWstatus_impl()
        return (Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_PreState_Container_PS(
          api_lower_alarm_temp = api_lower_alarm_temp.nextIsolette_Data_ModelTemp_impl(),
          api_upper_alarm_temp = high,
          api_current_tempWstatus = currentTemp,
          api_monitor_mode = api_monitor_mode.nextIsolette_Data_ModelMonitor_ModeType(),
          In_lastCmd = In_lastCmd.nextIsolette_Data_ModelOn_OffType()))
      }
      override def In_lastCmd: RandomLib = freshRandomLib
      override def api_lower_alarm_temp: RandomLib = freshRandomLib
      override def api_upper_alarm_temp: RandomLib = freshRandomLib
      override def api_current_tempWstatus: RandomLib = freshRandomLib
      override def api_monitor_mode: RandomLib = freshRandomLib
    })

  def configs: MSZ[UnitTestConfigurationBatch] = {
    def r(lb: F32, ub: F32, r: RandomLib): RandomLib = {
      return r.set_Config_F32(r.get_Config_F32(low = Some(lb), high = Some(ub)))
    }
    var c = defaultComputewLConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions)
    val p = c.profile.asInstanceOf[DefaultComputewLProfile]
    c = c(
      name = "Compute_Config_ranges_based_on_requirements",
      description = s"Ranges based on requirements - ie 96.0 <= lower_alarm <= 101.0 and 97.0 <= upper_alarm <= 102.  Also restrict current_temp to be between 86.0 and 112.0 inclusive",
      profile = p(
        api_lower_alarm_temp = r(96.0f, 101.0f, p.api_lower_alarm_temp),
        api_upper_alarm_temp = r(97.0f, 102.0f, p.api_upper_alarm_temp),
        api_current_tempWstatus = r(86.0f, 112.0f, p.api_current_tempWstatus)))
    return MSZ(
      defaultInitializeConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions),
      //defaultComputeConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions),
      //defaultComputewLConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions)
      c
    )
  }




  for (c <- configs) {
    def next: Option[Container] = {
      try {
        return Some(c.profile.next)
      } catch {
        case e: AssertionError => // SlangCheck was unable to satisfy a datatype's filter
          return None()
      }
    }

    for (i <- 0 until c.numTests) {
      val testName = s"${c.name}_$i"

      this.registerTest(testName) {
        var retry: B = T

        var j: Z = 0
        while (j < c.numTestVectorGenRetries && retry) {
          next match {
            case Some(o) =>

              if (verbose && j > 1) {
                println(s"Retry $j:")
              }

              val results = c.test(o)

              if (verbose) {
                c.genReplay(o, testName, results) match {
                  case Some(s) => println(s)
                  case _ =>
                }
              }

              results match {
                case GumboXResult.Pre_Condition_Unsat =>
                case GumboXResult.Post_Condition_Fail =>
                  fail("Post condition did not hold")
                  retry = F
                case GumboXResult.Post_Condition_Pass =>
                  if (verbose) {
                    println("Success!")
                  }
                  retry = F
              }
            case _ =>

          }
          j = j + 1
        }

        if (retry) {
          if (c.failOnUnsatPreconditions) {
            fail("Unable to satisfy precondition")
          } else if (verbose) {
            cprintln(T, "Unable to satisfy precondition")
          }
        }
      }
    }
  }

  def configsToJson: String = {
    return st"[ ${(for (c <- configs) yield s"\"${c.name}|${c.description}\"", ", ")} ]".render
  }
}
