# <!--start__rts-title-->RTS<!--end____rts-title-->
<!--start__rts-description-->
The data, links, and images in this file are auto-generated from HAMR's report generation facility. Additional text explanations have been added for readability.
<!--end____rts-description-->
## <!--start__rts_arch-section-title-->AADL Architecture<!--end____rts_arch-section-title-->
<!--start__rts_arch-section-description-->
![AADL Arch](aadl/diagrams/aadl-arch.svg)

The following documentation blocks provide links to AADL textual representation source of the Thread components in the system.
* "Type" links to the AADL component type declaration (providing the port-based interface for the component)
* "Behavior Specification" (when present) links to the GUMBO behavior contract for the component. HAMR automatically
compiles the GUMBO contract to both an code-level contract used for Logika code verification as well as an executable
representation of the contract (as pure boolean functions) used in unit and system testing.
<!--end____rts_arch-section-description-->
<!--start__rts_arch-section_aadl-arch-component-info-rts_i_instance-->
|System: [RTS::RTS.i](aadl/packages/RTS.aadl#L35)|
|:--|
<!--end____rts_arch-section_aadl-arch-component-info-rts_i_instance-->
<!--start__rts_arch-section_aadl-arch-component-info-actuator-tpau_temppressa_actuator-->
|Thread: TPAU_tempPressA_actuator <!--[actuator](aadl/packages/Actuation.aadl#L240)--> |
|:--|
|Type: [Actuation::Actuator](aadl/packages/Actuation.aadl#L187)<br>Implementation: [Actuation::Actuator.i](aadl/packages/Actuation.aadl#L216)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L202)|
|Periodic: 1000 ms|
|Domain: 14|

<!--end____rts_arch-section_aadl-arch-component-info-actuator-tpau_temppressa_actuator-->
<!--start__rts_arch-section_aadl-arch-component-info-actuator-sau_satactuator_actuator-->
|Thread: SAU_satActuator_actuator <!--[actuator](aadl/packages/Actuation.aadl#L240)--> |
|:--|
|Type: [Actuation::Actuator](aadl/packages/Actuation.aadl#L187)<br>Implementation: [Actuation::Actuator.i](aadl/packages/Actuation.aadl#L216)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L202)|
|Periodic: 1000 ms|
|Domain: 16|

<!--end____rts_arch-section_aadl-arch-component-info-actuator-sau_satactuator_actuator-->
<!--start__rts_arch-section_aadl-arch-component-info-coincidencelogic-au1_temp_coincidencelogic-->
|Thread: au1_temp_coincidenceLogic <!--[coincidenceLogic](aadl/packages/Actuation.aadl#L86)--> |
|:--|
|Type: [Actuation::CoincidenceLogic](aadl/packages/Actuation.aadl#L22)<br>Implementation: [Actuation::CoincidenceLogic.i](aadl/packages/Actuation.aadl#L59)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L40)|
|Periodic: 1000 ms|
|Domain: 5|

<!--end____rts_arch-section_aadl-arch-component-info-coincidencelogic-au1_temp_coincidencelogic-->
<!--start__rts_arch-section_aadl-arch-component-info-coincidencelogic-au1_press_coincidencelogic-->
|Thread: au1_press_coincidenceLogic <!--[coincidenceLogic](aadl/packages/Actuation.aadl#L86)--> |
|:--|
|Type: [Actuation::CoincidenceLogic](aadl/packages/Actuation.aadl#L22)<br>Implementation: [Actuation::CoincidenceLogic.i](aadl/packages/Actuation.aadl#L59)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L40)|
|Periodic: 1000 ms|
|Domain: 6|

<!--end____rts_arch-section_aadl-arch-component-info-coincidencelogic-au1_press_coincidencelogic-->
<!--start__rts_arch-section_aadl-arch-component-info-coincidencelogic-au1_satlogic_coincidencelogic-->
|Thread: au1_satLogic_coincidenceLogic <!--[coincidenceLogic](aadl/packages/Actuation.aadl#L86)--> |
|:--|
|Type: [Actuation::CoincidenceLogic](aadl/packages/Actuation.aadl#L22)<br>Implementation: [Actuation::CoincidenceLogic.i](aadl/packages/Actuation.aadl#L59)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L40)|
|Periodic: 1000 ms|
|Domain: 7|

<!--end____rts_arch-section_aadl-arch-component-info-coincidencelogic-au1_satlogic_coincidencelogic-->
<!--start__rts_arch-section_aadl-arch-component-info-coincidencelogic-au2_temp_coincidencelogic-->
|Thread: au2_temp_coincidenceLogic <!--[coincidenceLogic](aadl/packages/Actuation.aadl#L86)--> |
|:--|
|Type: [Actuation::CoincidenceLogic](aadl/packages/Actuation.aadl#L22)<br>Implementation: [Actuation::CoincidenceLogic.i](aadl/packages/Actuation.aadl#L59)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L40)|
|Periodic: 1000 ms|
|Domain: 9|

<!--end____rts_arch-section_aadl-arch-component-info-coincidencelogic-au2_temp_coincidencelogic-->
<!--start__rts_arch-section_aadl-arch-component-info-coincidencelogic-au2_press_coincidencelogic-->
|Thread: au2_press_coincidenceLogic <!--[coincidenceLogic](aadl/packages/Actuation.aadl#L86)--> |
|:--|
|Type: [Actuation::CoincidenceLogic](aadl/packages/Actuation.aadl#L22)<br>Implementation: [Actuation::CoincidenceLogic.i](aadl/packages/Actuation.aadl#L59)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L40)|
|Periodic: 1000 ms|
|Domain: 10|

<!--end____rts_arch-section_aadl-arch-component-info-coincidencelogic-au2_press_coincidencelogic-->
<!--start__rts_arch-section_aadl-arch-component-info-coincidencelogic-au2_sat_coincidencelogic-->
|Thread: au2_sat_coincidenceLogic <!--[coincidenceLogic](aadl/packages/Actuation.aadl#L86)--> |
|:--|
|Type: [Actuation::CoincidenceLogic](aadl/packages/Actuation.aadl#L22)<br>Implementation: [Actuation::CoincidenceLogic.i](aadl/packages/Actuation.aadl#L59)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L40)|
|Periodic: 1000 ms|
|Domain: 11|

<!--end____rts_arch-section_aadl-arch-component-info-coincidencelogic-au2_sat_coincidencelogic-->
<!--start__rts_arch-section_aadl-arch-component-info-orlogic-au1_temppresstripout_orlogic-->
|Thread: au1_tempPressTripOut_orLogic <!--[orLogic](aadl/packages/Actuation.aadl#L152)--> |
|:--|
|Type: [Actuation::OrLogic](aadl/packages/Actuation.aadl#L101)<br>Implementation: [Actuation::OrLogic.i](aadl/packages/Actuation.aadl#L128)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L119)|
|Periodic: 1000 ms|
|Domain: 8|

<!--end____rts_arch-section_aadl-arch-component-info-orlogic-au1_temppresstripout_orlogic-->
<!--start__rts_arch-section_aadl-arch-component-info-orlogic-au2_temppresstripout_orlogic-->
|Thread: au2_tempPressTripOut_orLogic <!--[orLogic](aadl/packages/Actuation.aadl#L152)--> |
|:--|
|Type: [Actuation::OrLogic](aadl/packages/Actuation.aadl#L101)<br>Implementation: [Actuation::OrLogic.i](aadl/packages/Actuation.aadl#L128)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L119)|
|Periodic: 1000 ms|
|Domain: 12|

<!--end____rts_arch-section_aadl-arch-component-info-orlogic-au2_temppresstripout_orlogic-->
<!--start__rts_arch-section_aadl-arch-component-info-orlogic-tpau_acttemppa_orlogic-->
|Thread: TPAU_actTempPA_orLogic <!--[orLogic](aadl/packages/Actuation.aadl#L152)--> |
|:--|
|Type: [Actuation::OrLogic](aadl/packages/Actuation.aadl#L101)<br>Implementation: [Actuation::OrLogic.i](aadl/packages/Actuation.aadl#L128)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L119)|
|Periodic: 1000 ms|
|Domain: 13|

<!--end____rts_arch-section_aadl-arch-component-info-orlogic-tpau_acttemppa_orlogic-->
<!--start__rts_arch-section_aadl-arch-component-info-orlogic-sau_actsatactuator_orlogic-->
|Thread: SAU_actSatActuator_orLogic <!--[orLogic](aadl/packages/Actuation.aadl#L152)--> |
|:--|
|Type: [Actuation::OrLogic](aadl/packages/Actuation.aadl#L101)<br>Implementation: [Actuation::OrLogic.i](aadl/packages/Actuation.aadl#L128)<br>Behavior Specification: [GUMBO](aadl/packages/Actuation.aadl#L119)|
|Periodic: 1000 ms|
|Domain: 15|

<!--end____rts_arch-section_aadl-arch-component-info-orlogic-sau_actsatactuator_orlogic-->
<!--start__rts_arch-section_aadl-arch-component-info-actuatorsmockthread-actuatorsmockthread-->
|Thread: actuatorsMockThread <!--[actuatorsMockThread](aadl/packages/Actuators.aadl#L48)--> |
|:--|
|Type: [Actuators::ActuatorsMockThread](aadl/packages/Actuators.aadl#L16)<br>Implementation: [Actuators::ActuatorsMockThread.i](aadl/packages/Actuators.aadl#L23)|
|Periodic: 1000 ms|
|Domain: 4|

<!--end____rts_arch-section_aadl-arch-component-info-actuatorsmockthread-actuatorsmockthread-->
<!--start__rts_arch-section_aadl-arch-component-info-eventcontrolmockthread-eventcontrolmockthread-->
|Thread: eventControlMockThread <!--[eventControlMockThread](aadl/packages/EventControl.aadl#L69)--> |
|:--|
|Type: [EventControl::EventControlMockThread](aadl/packages/EventControl.aadl#L16)<br>Implementation: [EventControl::EventControlMockThread.i](aadl/packages/EventControl.aadl#L37)|
|Periodic: 1000 ms|
|Domain: 3|

<!--end____rts_arch-section_aadl-arch-component-info-eventcontrolmockthread-eventcontrolmockthread-->
<!--start__rts_arch-section_aadl-arch-component-info-instrumentationmockthread-instrumentationmockthread-->
|Thread: instrumentationMockThread <!--[instrumentationMockThread](aadl/packages/Instrumentation.aadl#L195)--> |
|:--|
|Type: [Instrumentation::InstrumentationMockThread](aadl/packages/Instrumentation.aadl#L14)<br>Implementation: [Instrumentation::InstrumentationMockThread.i](aadl/packages/Instrumentation.aadl#L97)|
|Periodic: 1000 ms|
|Domain: 2|

<!--end____rts_arch-section_aadl-arch-component-info-instrumentationmockthread-instrumentationmockthread-->

## <!--start__rts_behavior-code-title-->Behavior Code<!--end____rts_behavior-code-title-->
<!--start__rts_behavior-code-description-->
The following items link to the Slang source code for the application logic of each thread.
In the HAMR development workflow, skeletons for these files are automatically created,
along with APIs for communicating over model-declared ports in the component type.
GUMBO component contracts in the AADL model are automatically translated to Slang/Logika
contracts and included in the generated skeletons. Then, the application developer uses a
conventional development approach for coding the application logic in Slang
(C workflows are also supported). Logika can be applied to verify that the user's
application code conforms to the generated Logika contracts (which are derived
automatically from model-level GUMBO contracts). The HAMR build framework will integrate
the user-code application logic for each component (below) with auto-generated threading
and communication infrastructure code, along with HAMR's implementation of AADL run-time
(based on AADL's standardized Run-Time Services). Note that HAMR is smart enough to
accommodate changes to model-level interface declarations (ports, etc.) as well as changes
to GUMBO contracts -- user code will not be clobbered when the model is changed and HAMR
code generation is rerun. Instead, HAMR uses specially designed delimiters in the
application code files to, e.g., re-weave updated contracts into the application code.

Executable Slang versions of the GUMBO contracts (referred to as "GUMBOX" contracts)
are also automatically generated in the code generation process. These executable
contracts are automatically integrated into the unit testing process: appropriate
portions of the executable contracts are invoked in the pre-state and the post-state
of a thread dispatch to dynamically check that the thread's behavior for that particular
dispatch conforms to the model-level GUMBO contracts.
<!--end____rts_behavior-code-description-->
<!--start__rts_behavior-code_slang-code-instrumentationmockthread-instrumentationmockthread-->
[instrumentationMockThread](hamr/slang/src/main/component/RTS/Instrumentation/InstrumentationMockThread_i_instrumentationMock_instrumentationMockThread.scala)

<!--end____rts_behavior-code_slang-code-instrumentationmockthread-instrumentationmockthread-->
<!--start__rts_behavior-code_slang-code-eventcontrolmockthread-eventcontrolmockthread-->
[eventControlMockThread](hamr/slang/src/main/component/RTS/EventControl/EventControlMockThread_i_eventControlMock_eventControlMockThread.scala)

<!--end____rts_behavior-code_slang-code-eventcontrolmockthread-eventcontrolmockthread-->
<!--start__rts_behavior-code_slang-code-actuatorsmockthread-actuatorsmockthread-->
[actuatorsMockThread](hamr/slang/src/main/component/RTS/Actuators/ActuatorsMockThread_i_actuatorsMock_actuatorsMockThread.scala)

<!--end____rts_behavior-code_slang-code-actuatorsmockthread-actuatorsmockthread-->
<!--start__rts_behavior-code_slang-code-coincidencelogic-au1_temp_coincidencelogic-->
[au1_temp_coincidenceLogic](hamr/slang/src/main/component/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_temperatureLogic_coincidenceLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_temperatureLogic_coincidenceLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-coincidencelogic-au1_temp_coincidencelogic-->
<!--start__rts_behavior-code_slang-code-coincidencelogic-au1_press_coincidencelogic-->
[au1_press_coincidenceLogic](hamr/slang/src/main/component/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_pressureLogic_coincidenceLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_pressureLogic_coincidenceLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-coincidencelogic-au1_press_coincidencelogic-->
<!--start__rts_behavior-code_slang-code-coincidencelogic-au1_satlogic_coincidencelogic-->
[au1_satLogic_coincidenceLogic](hamr/slang/src/main/component/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_saturationLogic_coincidenceLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_saturationLogic_coincidenceLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-coincidencelogic-au1_satlogic_coincidencelogic-->
<!--start__rts_behavior-code_slang-code-orlogic-au1_temppresstripout_orlogic-->
[au1_tempPressTripOut_orLogic](hamr/slang/src/main/component/RTS/Actuation/OrLogic_i_actuationSubsystem_actuationUnit1_tempPressureTripOut_orLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/OrLogic_i_actuationSubsystem_actuationUnit1_tempPressureTripOut_orLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-orlogic-au1_temppresstripout_orlogic-->
<!--start__rts_behavior-code_slang-code-coincidencelogic-au2_temp_coincidencelogic-->
[au2_temp_coincidenceLogic](hamr/slang/src/main/component/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_temperatureLogic_coincidenceLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_temperatureLogic_coincidenceLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-coincidencelogic-au2_temp_coincidencelogic-->
<!--start__rts_behavior-code_slang-code-coincidencelogic-au2_press_coincidencelogic-->
[au2_press_coincidenceLogic](hamr/slang/src/main/component/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_pressureLogic_coincidenceLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_pressureLogic_coincidenceLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-coincidencelogic-au2_press_coincidencelogic-->
<!--start__rts_behavior-code_slang-code-coincidencelogic-au2_sat_coincidencelogic-->
[au2_sat_coincidenceLogic](hamr/slang/src/main/component/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_saturationLogic_coincidenceLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_saturationLogic_coincidenceLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-coincidencelogic-au2_sat_coincidencelogic-->
<!--start__rts_behavior-code_slang-code-orlogic-au2_temppresstripout_orlogic-->
[au2_tempPressTripOut_orLogic](hamr/slang/src/main/component/RTS/Actuation/OrLogic_i_actuationSubsystem_actuationUnit2_tempPressureTripOut_orLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/OrLogic_i_actuationSubsystem_actuationUnit2_tempPressureTripOut_orLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-orlogic-au2_temppresstripout_orlogic-->
<!--start__rts_behavior-code_slang-code-orlogic-tpau_acttemppa_orlogic-->
[TPAU_actTempPA_orLogic](hamr/slang/src/main/component/RTS/Actuation/OrLogic_i_actuationSubsystem_tempPressureActuatorUnit_actuateTempPressureActuator_orLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/OrLogic_i_actuationSubsystem_tempPressureActuatorUnit_actuateTempPressureActuator_orLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-orlogic-tpau_acttemppa_orlogic-->
<!--start__rts_behavior-code_slang-code-actuator-tpau_temppressa_actuator-->
[TPAU_tempPressA_actuator](hamr/slang/src/main/component/RTS/Actuation/Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator_GumboX.scala)
<!--end____rts_behavior-code_slang-code-actuator-tpau_temppressa_actuator-->
<!--start__rts_behavior-code_slang-code-orlogic-sau_actsatactuator_orlogic-->
[SAU_actSatActuator_orLogic](hamr/slang/src/main/component/RTS/Actuation/OrLogic_i_actuationSubsystem_saturationActuatorUnit_actuateSaturationActuator_orLogic.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/OrLogic_i_actuationSubsystem_saturationActuatorUnit_actuateSaturationActuator_orLogic_GumboX.scala)
<!--end____rts_behavior-code_slang-code-orlogic-sau_actsatactuator_orlogic-->
<!--start__rts_behavior-code_slang-code-actuator-sau_satactuator_actuator-->
[SAU_satActuator_actuator](hamr/slang/src/main/component/RTS/Actuation/Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator.scala)
<br>[GumboX](hamr/slang/src/main/bridge/RTS/Actuation/Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator_GumboX.scala)
<!--end____rts_behavior-code_slang-code-actuator-sau_satactuator_actuator-->

## <!--start__rts_metrics-title-->Metrics<!--end____rts_metrics-title-->
<!--start__rts_metrics-description-->
<!--end____rts_metrics-description-->
### <!--start__rts_metrics_aadl-metrics-title-->AADL Metrics<!--end____rts_metrics_aadl-metrics-title-->
<!--start__rts_metrics_aadl-metrics-description-->
The following section provides statistics about the AADL model to give a rough idea of
its size (in terms of number of AADL modeling elements that impact the size of the deployed system).
<!--end____rts_metrics_aadl-metrics-description-->
<!--start__rts_metrics_aadl-metrics_aadl-metrics-content-block-->
| | |
|:--|:--|
|Threads|15|
|Ports|76|
|Connections|38|
<!--end____rts_metrics_aadl-metrics_aadl-metrics-content-block-->

### <!--start__rts_metrics_jvm-metrics-title-->JVM Metrics<!--end____rts_metrics_jvm-metrics-title-->
<!--start__rts_metrics_jvm-metrics-description-->
The following section provides statistics about the Slang source code.
<!--end____rts_metrics_jvm-metrics-description-->
<!--start__rts_metrics_jvm-metrics_rts_code_metrics-->
Directories Scanned Using [https://github.com/AlDanial/cloc](https://github.com/AlDanial/cloc) v1.94:
- [hamr/slang/src/main](hamr/slang/src/main)

<u><b>Total LOC</b></u>

Total number of HAMR-generated and developer-written lines of code
Language|files|blank|comment|code
:-------|-------:|-------:|-------:|-------:
Scala|175|5991|2558|25421
--------|--------|--------|--------|--------
SUM:|175|5991|2558|25421

<u><b>User LOC</b></u>

The number of lines of code written by the developer.
"Log" are lines of code used for logging that
likely would be excluded in a release build
 |Type|code |
 |:--|--:|
 |Behavior|156|
 |Log|6|
 |--------|--------|
 |SUM:|162|
<!--end____rts_metrics_jvm-metrics_rts_code_metrics-->

## <!--start__rts_gumbox-unit-testing-setup-title-->GUMBOX Unit Testing<!--end____rts_gumbox-unit-testing-setup-title-->
<!--start__rts_gumbox-unit-testing-setup-description-->
<!--end____rts_gumbox-unit-testing-setup-description-->
### <!--start__rts_gumbox-unit-testing-setup_configurations-title-->Unit Test Run Configurations<!--end____rts_gumbox-unit-testing-setup_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations-description-->
<!--end____rts_gumbox-unit-testing-setup_configurations-description-->
#### <!--start__rts_gumbox-unit-testing-setup_configurations_sau_satactuator_actuator_configurations-title-->SAU_satActuator_actuator<!--end____rts_gumbox-unit-testing-setup_configurations_sau_satactuator_actuator_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_sau_satactuator_actuator_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator_DSC_UnitTests/report.html)

    - Configurations for the Initialize Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator_UnitTestConfiguration_Util.scala#L22">Default_Initialize_Config</a></td><td>Default Initialize Configuration</td></tr>
      </table>

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator_UnitTestConfiguration_Util.scala#L38">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_sau_satactuator_actuator_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_tpau_temppressa_actuator_configurations-title-->TPAU_tempPressA_actuator<!--end____rts_gumbox-unit-testing-setup_configurations_tpau_temppressa_actuator_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_tpau_temppressa_actuator_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator_DSC_UnitTests/report.html)

    - Configurations for the Initialize Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator_UnitTestConfiguration_Util.scala#L22">Default_Initialize_Config</a></td><td>Default Initialize Configuration</td></tr>
      </table>

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator_UnitTestConfiguration_Util.scala#L38">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_tpau_temppressa_actuator_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_au1_press_coincidencelogic_configurations-title-->au1_press_coincidenceLogic<!--end____rts_gumbox-unit-testing-setup_configurations_au1_press_coincidencelogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_au1_press_coincidencelogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_pressureLogic_coincidenceLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_pressureLogic_coincidenceLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_pressureLogic_coincidenceLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_au1_press_coincidencelogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_au1_satlogic_coincidencelogic_configurations-title-->au1_satLogic_coincidenceLogic<!--end____rts_gumbox-unit-testing-setup_configurations_au1_satlogic_coincidencelogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_au1_satlogic_coincidencelogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_saturationLogic_coincidenceLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_saturationLogic_coincidenceLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_saturationLogic_coincidenceLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_au1_satlogic_coincidencelogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_au1_temp_coincidencelogic_configurations-title-->au1_temp_coincidenceLogic<!--end____rts_gumbox-unit-testing-setup_configurations_au1_temp_coincidencelogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_au1_temp_coincidencelogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_temperatureLogic_coincidenceLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_temperatureLogic_coincidenceLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_temperatureLogic_coincidenceLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_au1_temp_coincidencelogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_au2_press_coincidencelogic_configurations-title-->au2_press_coincidenceLogic<!--end____rts_gumbox-unit-testing-setup_configurations_au2_press_coincidencelogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_au2_press_coincidencelogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_pressureLogic_coincidenceLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_pressureLogic_coincidenceLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_pressureLogic_coincidenceLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_au2_press_coincidencelogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_au2_sat_coincidencelogic_configurations-title-->au2_sat_coincidenceLogic<!--end____rts_gumbox-unit-testing-setup_configurations_au2_sat_coincidencelogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_au2_sat_coincidencelogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_saturationLogic_coincidenceLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_saturationLogic_coincidenceLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_saturationLogic_coincidenceLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_au2_sat_coincidencelogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_au2_temp_coincidencelogic_configurations-title-->au2_temp_coincidenceLogic<!--end____rts_gumbox-unit-testing-setup_configurations_au2_temp_coincidencelogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_au2_temp_coincidencelogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_temperatureLogic_coincidenceLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_temperatureLogic_coincidenceLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_temperatureLogic_coincidenceLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_au2_temp_coincidencelogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_au1_temppresstripout_orlogic_configurations-title-->au1_tempPressTripOut_orLogic<!--end____rts_gumbox-unit-testing-setup_configurations_au1_temppresstripout_orlogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_au1_temppresstripout_orlogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/OrLogic_i_actuationSubsystem_actuationUnit1_tempPressureTripOut_orLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/OrLogic_i_actuationSubsystem_actuationUnit1_tempPressureTripOut_orLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/OrLogic_i_actuationSubsystem_actuationUnit1_tempPressureTripOut_orLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_au1_temppresstripout_orlogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_au2_temppresstripout_orlogic_configurations-title-->au2_tempPressTripOut_orLogic<!--end____rts_gumbox-unit-testing-setup_configurations_au2_temppresstripout_orlogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_au2_temppresstripout_orlogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/OrLogic_i_actuationSubsystem_actuationUnit2_tempPressureTripOut_orLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/OrLogic_i_actuationSubsystem_actuationUnit2_tempPressureTripOut_orLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/OrLogic_i_actuationSubsystem_actuationUnit2_tempPressureTripOut_orLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_au2_temppresstripout_orlogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_sau_actsatactuator_orlogic_configurations-title-->SAU_actSatActuator_orLogic<!--end____rts_gumbox-unit-testing-setup_configurations_sau_actsatactuator_orlogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_sau_actsatactuator_orlogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/OrLogic_i_actuationSubsystem_saturationActuatorUnit_actuateSaturationActuator_orLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/OrLogic_i_actuationSubsystem_saturationActuatorUnit_actuateSaturationActuator_orLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/OrLogic_i_actuationSubsystem_saturationActuatorUnit_actuateSaturationActuator_orLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_sau_actsatactuator_orlogic_configurations-description-->

#### <!--start__rts_gumbox-unit-testing-setup_configurations_tpau_acttemppa_orlogic_configurations-title-->TPAU_actTempPA_orLogic<!--end____rts_gumbox-unit-testing-setup_configurations_tpau_acttemppa_orlogic_configurations-title-->
<!--start__rts_gumbox-unit-testing-setup_configurations_tpau_acttemppa_orlogic_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/RTS/Actuation/OrLogic_i_actuationSubsystem_tempPressureActuatorUnit_actuateTempPressureActuator_orLogic_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/rts/OrLogic_i_actuationSubsystem_tempPressureActuatorUnit_actuateTempPressureActuator_orLogic_DSC_UnitTests/report.html)

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/RTS/Actuation/OrLogic_i_actuationSubsystem_tempPressureActuatorUnit_actuateTempPressureActuator_orLogic_UnitTestConfiguration_Util.scala#L22">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____rts_gumbox-unit-testing-setup_configurations_tpau_acttemppa_orlogic_configurations-description-->
