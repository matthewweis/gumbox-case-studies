package tc.TempControlSoftwareSystem

import org.sireum._
import tc.GumboXUtil.GumboXResult
import tc.RandomLib
import tc.util.{Container, UnitTestConfigurationBatch}
import tc.TempControlSoftwareSystem.TempControlPeriodic_p_tcproc_tempControl_UnitTestConfiguration_Util._

// This file will not be overwritten so is safe to edit

class TempControlPeriodic_p_tcproc_tempControl_GumboX_UnitTests extends TempControlPeriodic_p_tcproc_tempControl_GumboX_TestHarness_ScalaTest {

  val verbose: B = T
  val failOnUnsatPreconditions: B = T

  def configs: MSZ[UnitTestConfigurationBatch] = {
    def r(lb: F32, ub: F32, r: RandomLib): RandomLib = {
      return r.set_Config_F32(r.get_Config_F32(low = Some(lb), high = Some(ub)))
    }
    var c = defaultComputewLConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions)
    val p = c.profile.asInstanceOf[DefaultComputewLProfile]
    c = c(
      name = "Custom_ComputewL_Config",
      description = "Set ranges based on requirements - i.e. 50 <= SetPoint.Low and SetPoint.High <= 110 and -128 <= current_temp <= 134",
      profile = p(
        api_currentTemp = r(-128.0f, 134.0f, p.api_currentTemp),
        api_setPoint = r(50, 110, p.api_setPoint)))
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
