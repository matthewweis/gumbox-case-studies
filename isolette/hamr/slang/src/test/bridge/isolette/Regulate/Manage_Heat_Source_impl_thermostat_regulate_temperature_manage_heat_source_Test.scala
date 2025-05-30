package isolette.Regulate

import org.sireum._
import isolette.Regulate._
import isolette.Isolette_Data_Model._

// This file will not be overwritten so is safe to edit
class Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_Test extends Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_ScalaTest {

  test("Example Unit Test for Initialise Entry Point"){
    // Initialise Entry Point doesn't read input port values, so just proceed with
    // launching the entry point code
    testInitialise()
    // use get_XXX methods and check_concrete_output() from test/util/../YYY_TestApi
    // retrieve values from output ports and check against expected results
  }

  test("Example Unit Test for Compute Entry Point"){
    // use put_XXX methods from test/util/../YYY_TestApi to seed input ports with values
    //testCompute()
    // use get_XXX methods and check_concrete_output() from test/util/../YYY_TestApi
    // retrieve values from output ports and check against expected results
  }

  // helper function to check for expected concrete value for heat control
  def check_expected_heat_control(heat_control: On_Off.Type): Unit = {
    // pass a lambda to grab output and check for desired output
    check_concrete_output(c => c == heat_control)
  }

  // REQ-MHS-2: If the Regulator Mode is NORMAL and the Current Temperature is less than
  // the Lower Desired Temperature, the Heat Control shall be set to On.
  test("Req-MHS-2: Normal, Low Temp") {
    // --------- inputs ----------
    // Note: the values here should also satisfy the range constraints on
    // temperature values (see FAA REMH Table A-3 and A-5)
    put_concrete_inputs(
      // relevant to requirement
      TempWstatus_impl(95f, ValueStatus.Valid), // Current Temperature
      Temp_impl(97f), // Lower Desired Temperature
      // Upper Desired Temperature below is irrelevant to requirement
      Temp_impl(102f),
      // relevant to requirement
      Regulator_Mode.Normal_Regulator_Mode
    )

    // execute compute entry point
    testCompute()

    // --------- outputs ----------
    check_expected_heat_control(On_Off.Onn)
  }

  // ================== Illustrate baseline APIs (simple puts and gets for ports) ======================

  // REQ-MHS-2: If the Regulator Mode is NORMAL and the Current Temperature is less than
  // the Lower Desired Temperature, the Heat Control shall be set to On.
  test("Req-MHS-2: Normal, Low Temp (baseline APIs)") {
    // --------- put inputs ----------
    // Note: the values here should also satisfy the range constraints on
    // temperature values (see FAA REMH Table A-3 and A-5)
    put_current_tempWstatus(TempWstatus_impl(95f, ValueStatus.Valid))  // Current Temperature
    put_lower_desired_temp(Temp_impl(97f)) // Lower Desired Temperature
    put_upper_desired_temp(Temp_impl(100f)) // Upper Desired Temperature below is irrelevant to requirement
    put_regulator_mode(Regulator_Mode.Normal_Regulator_Mode) // Regulator Mode
    // execute compute entry point
    testCompute()

    // --------- get outputs ----------
    val heat_control_actual: On_Off.Type = get_heat_control().get

    // --------- compare outputs to expected results ----------
    val heat_control_expected: On_Off.Type = On_Off.Onn
    assert(heat_control_expected == heat_control_actual)
  }
}
