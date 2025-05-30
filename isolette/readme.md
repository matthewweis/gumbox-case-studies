# <!--start__isolette-title-->Isolette<!--end____isolette-title-->
<!--start__isolette-description-->
The data, links, and images in this file are auto-generated from HAMR's report generation facility. Additional text explanations have been added for readability.
<!--end____isolette-description-->
## <!--start__isolette_arch-section-title-->AADL Architecture<!--end____isolette_arch-section-title-->
<!--start__isolette_arch-section-description-->
![AADL Arch](aadl/diagrams/aadl-arch.svg)

The following documentation blocks provide links to AADL textual representation source of the Thread components in the system.
* "Type" links to the AADL component type declaration (providing the port-based interface for the component)
* "Behavior Specification" (when present) links to the GUMBO behavior contract for the component. HAMR automatically
compiles the GUMBO contract to both an code-level contract used for Logika code verification as well as an executable
representation of the contract (as pure boolean functions) used in unit and system testing.
<!--end____isolette_arch-section-description-->
<!--start__isolette_arch-section_aadl-arch-component-info-isolette_single_sensor_instance-->
|System: [Isolette::isolette.single_sensor](aadl/aadl/packages/Isolette.aadl#L71)|
|:--|
<!--end____isolette_arch-section_aadl-arch-component-info-isolette_single_sensor_instance-->
<!--start__isolette_arch-section_aadl-arch-component-info-heat_controller-hs-->
|Thread: HS <!--[heat_controller](aadl/aadl/packages/Devices.aadl#L118)--> |
|:--|
|Type: [Devices::Heat_Source](aadl/aadl/packages/Devices.aadl#L124)<br>Implementation: [Devices::Heat_Source.impl](aadl/aadl/packages/Devices.aadl#L135)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-heat_controller-hs-->
<!--start__isolette_arch-section_aadl-arch-component-info-thermostat-ts-->
|Thread: TS <!--[thermostat](aadl/aadl/packages/Devices.aadl#L73)--> |
|:--|
|Type: [Devices::Temperature_Sensor](aadl/aadl/packages/Devices.aadl#L79)<br>Implementation: [Devices::Temperature_Sensor.impl](aadl/aadl/packages/Devices.aadl#L90)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-thermostat-ts-->
<!--start__isolette_arch-section_aadl-arch-component-info-oit-opinterface-->
|Thread: OpInterface <!--[oit](aadl/aadl/packages/Isolette.aadl#L274)--> |
|:--|
|Type: [Isolette::operator_interface_thread](aadl/aadl/packages/Isolette.aadl#L287)<br>Implementation: [Isolette::operator_interface_thread.impl](aadl/aadl/packages/Isolette.aadl#L307)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-oit-opinterface-->
<!--start__isolette_arch-section_aadl-arch-component-info-detect_monitor_failure-dmf-->
|Thread: DMF <!--[detect_monitor_failure](aadl/aadl/packages/Monitor.aadl#L43)--> |
|:--|
|Type: [Monitor::Detect_Monitor_Failure](aadl/aadl/packages/Monitor.aadl#L423)<br>Implementation: [Monitor::Detect_Monitor_Failure.impl](aadl/aadl/packages/Monitor.aadl#L435)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-detect_monitor_failure-dmf-->
<!--start__isolette_arch-section_aadl-arch-component-info-manage_alarm-ma-->
|Thread: MA <!--[manage_alarm](aadl/aadl/packages/Monitor.aadl#L39)--> |
|:--|
|Type: [Monitor::Manage_Alarm](aadl/aadl/packages/Monitor.aadl#L297)<br>Implementation: [Monitor::Manage_Alarm.impl](aadl/aadl/packages/Monitor.aadl#L407)<br>Behavior Specification: [GUMBO](aadl/aadl/packages/Monitor.aadl#L317)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-manage_alarm-ma-->
<!--start__isolette_arch-section_aadl-arch-component-info-manage_monitor_interface-mmi-->
|Thread: MMI <!--[manage_monitor_interface](aadl/aadl/packages/Monitor.aadl#L37)--> |
|:--|
|Type: [Monitor::Manage_Monitor_Interface](aadl/aadl/packages/Monitor.aadl#L96)<br>Implementation: [Monitor::Manage_Monitor_Interface.impl](aadl/aadl/packages/Monitor.aadl#L204)<br>Behavior Specification: [GUMBO](aadl/aadl/packages/Monitor.aadl#L129)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-manage_monitor_interface-mmi-->
<!--start__isolette_arch-section_aadl-arch-component-info-manage_monitor_mode-mmm-->
|Thread: MMM <!--[manage_monitor_mode](aadl/aadl/packages/Monitor.aadl#L41)--> |
|:--|
|Type: [Monitor::Manage_Monitor_Mode](aadl/aadl/packages/Monitor.aadl#L218)<br>Implementation: [Monitor::Manage_Monitor_Mode.impl](aadl/aadl/packages/Monitor.aadl#L283)<br>Behavior Specification: [GUMBO](aadl/aadl/packages/Monitor.aadl#L236)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-manage_monitor_mode-mmm-->
<!--start__isolette_arch-section_aadl-arch-component-info-detect_regulator_failure-drf-->
|Thread: DRF <!--[detect_regulator_failure](aadl/aadl/packages/Regulate.aadl#L47)--> |
|:--|
|Type: [Regulate::Detect_Regulator_Failure](aadl/aadl/packages/Regulate.aadl#L505)<br>Implementation: [Regulate::Detect_Regulator_Failure.impl](aadl/aadl/packages/Regulate.aadl#L517)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-detect_regulator_failure-drf-->
<!--start__isolette_arch-section_aadl-arch-component-info-manage_heat_source-mhs-->
|Thread: MHS <!--[manage_heat_source](aadl/aadl/packages/Regulate.aadl#L41)--> |
|:--|
|Type: [Regulate::Manage_Heat_Source](aadl/aadl/packages/Regulate.aadl#L397)<br>Implementation: [Regulate::Manage_Heat_Source.impl](aadl/aadl/packages/Regulate.aadl#L488)<br>Behavior Specification: [GUMBO](aadl/aadl/packages/Regulate.aadl#L425)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-manage_heat_source-mhs-->
<!--start__isolette_arch-section_aadl-arch-component-info-manage_regulator_interface-mri-->
|Thread: MRI <!--[manage_regulator_interface](aadl/aadl/packages/Regulate.aadl#L37)--> |
|:--|
|Type: [Regulate::Manage_Regulator_Interface](aadl/aadl/packages/Regulate.aadl#L105)<br>Implementation: [Regulate::Manage_Regulator_Interface.impl](aadl/aadl/packages/Regulate.aadl#L255)<br>Behavior Specification: [GUMBO](aadl/aadl/packages/Regulate.aadl#L169)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-manage_regulator_interface-mri-->
<!--start__isolette_arch-section_aadl-arch-component-info-manage_regulator_mode-mrm-->
|Thread: MRM <!--[manage_regulator_mode](aadl/aadl/packages/Regulate.aadl#L45)--> |
|:--|
|Type: [Regulate::Manage_Regulator_Mode](aadl/aadl/packages/Regulate.aadl#L269)<br>Implementation: [Regulate::Manage_Regulator_Mode.impl](aadl/aadl/packages/Regulate.aadl#L382)<br>Behavior Specification: [GUMBO](aadl/aadl/packages/Regulate.aadl#L295)|
|Periodic: 1000 ms|

<!--end____isolette_arch-section_aadl-arch-component-info-manage_regulator_mode-mrm-->

## <!--start__isolette_behavior-code-title-->Behavior Code<!--end____isolette_behavior-code-title-->
<!--start__isolette_behavior-code-description-->
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
<!--end____isolette_behavior-code-description-->
<!--start__isolette_behavior-code_slang-code-manage_regulator_interface-mri-->
[MRI](hamr/slang/src/main/component/isolette/Regulate/Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface.scala)
<br>[GumboX](hamr/slang/src/main/bridge/isolette/Regulate/Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface_GumboX.scala)
<!--end____isolette_behavior-code_slang-code-manage_regulator_interface-mri-->
<!--start__isolette_behavior-code_slang-code-manage_heat_source-mhs-->
[MHS](hamr/slang/src/main/component/isolette/Regulate/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source.scala)
<br>[GumboX](hamr/slang/src/main/bridge/isolette/Regulate/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_GumboX.scala)
<!--end____isolette_behavior-code_slang-code-manage_heat_source-mhs-->
<!--start__isolette_behavior-code_slang-code-manage_regulator_mode-mrm-->
[MRM](hamr/slang/src/main/component/isolette/Regulate/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode.scala)
<br>[GumboX](hamr/slang/src/main/bridge/isolette/Regulate/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode_GumboX.scala)
<!--end____isolette_behavior-code_slang-code-manage_regulator_mode-mrm-->
<!--start__isolette_behavior-code_slang-code-detect_regulator_failure-drf-->
[DRF](hamr/slang/src/main/component/isolette/Regulate/Detect_Regulator_Failure_impl_thermostat_regulate_temperature_detect_regulator_failure.scala)

<!--end____isolette_behavior-code_slang-code-detect_regulator_failure-drf-->
<!--start__isolette_behavior-code_slang-code-manage_monitor_interface-mmi-->
[MMI](hamr/slang/src/main/component/isolette/Monitor/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface.scala)
<br>[GumboX](hamr/slang/src/main/bridge/isolette/Monitor/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface_GumboX.scala)
<!--end____isolette_behavior-code_slang-code-manage_monitor_interface-mmi-->
<!--start__isolette_behavior-code_slang-code-manage_alarm-ma-->
[MA](hamr/slang/src/main/component/isolette/Monitor/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm.scala)
<br>[GumboX](hamr/slang/src/main/bridge/isolette/Monitor/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_GumboX.scala)
<!--end____isolette_behavior-code_slang-code-manage_alarm-ma-->
<!--start__isolette_behavior-code_slang-code-manage_monitor_mode-mmm-->
[MMM](hamr/slang/src/main/component/isolette/Monitor/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode.scala)
<br>[GumboX](hamr/slang/src/main/bridge/isolette/Monitor/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode_GumboX.scala)
<!--end____isolette_behavior-code_slang-code-manage_monitor_mode-mmm-->
<!--start__isolette_behavior-code_slang-code-detect_monitor_failure-dmf-->
[DMF](hamr/slang/src/main/component/isolette/Monitor/Detect_Monitor_Failure_impl_thermostat_monitor_temperature_detect_monitor_failure.scala)

<!--end____isolette_behavior-code_slang-code-detect_monitor_failure-dmf-->
<!--start__isolette_behavior-code_slang-code-oit-opinterface-->
[OpInterface](hamr/slang/src/main/component/isolette/Isolette/operator_interface_thread_impl_operator_interface_oip_oit.scala)

<!--end____isolette_behavior-code_slang-code-oit-opinterface-->
<!--start__isolette_behavior-code_slang-code-thermostat-ts-->
[TS](hamr/slang/src/main/component/isolette/Devices/Temperature_Sensor_impl_temperature_sensor_cpi_thermostat.scala)

<!--end____isolette_behavior-code_slang-code-thermostat-ts-->
<!--start__isolette_behavior-code_slang-code-heat_controller-hs-->
[HS](hamr/slang/src/main/component/isolette/Devices/Heat_Source_impl_heat_source_cpi_heat_controller.scala)

<!--end____isolette_behavior-code_slang-code-heat_controller-hs-->

## <!--start__isolette_metrics-title-->Metrics<!--end____isolette_metrics-title-->
<!--start__isolette_metrics-description-->
<!--end____isolette_metrics-description-->
### <!--start__isolette_metrics_aadl-metrics-title-->AADL Metrics<!--end____isolette_metrics_aadl-metrics-title-->
<!--start__isolette_metrics_aadl-metrics-description-->
The following section provides statistics about the AADL model to give a rough idea of
its size (in terms of number of AADL modeling elements that impact the size of the deployed system).
<!--end____isolette_metrics_aadl-metrics-description-->
<!--start__isolette_metrics_aadl-metrics_aadl-metrics-content-block-->
| | |
|:--|:--|
|Threads|11|
|Ports|49|
|Connections|27|
<!--end____isolette_metrics_aadl-metrics_aadl-metrics-content-block-->

### <!--start__isolette_metrics_jvm-metrics-title-->JVM Metrics<!--end____isolette_metrics_jvm-metrics-title-->
<!--start__isolette_metrics_jvm-metrics-description-->
The following section provides statistics about the Slang source code.
<!--end____isolette_metrics_jvm-metrics-description-->
<!--start__isolette_metrics_jvm-metrics_isolette_code_metrics-->
Directories Scanned Using [https://github.com/AlDanial/cloc](https://github.com/AlDanial/cloc) v1.94:
- [hamr/slang/src/main](hamr/slang/src/main)

<u><b>Total LOC</b></u>

Total number of HAMR-generated and developer-written lines of code
Language|files|blank|comment|code
:-------|-------:|-------:|-------:|-------:
Scala|121|4483|2493|19954
--------|--------|--------|--------|--------
SUM:|121|4483|2493|19954

<u><b>User LOC</b></u>

The number of lines of code written by the developer.
"Log" are lines of code used for logging that
likely would be excluded in a release build
 |Type|code |
 |:--|--:|
 |Behavior|184|
 |Log|16|
 |--------|--------|
 |SUM:|200|
<!--end____isolette_metrics_jvm-metrics_isolette_code_metrics-->

## <!--start__isolette_gumbox-unit-testing-setup-title-->GUMBOX Unit Testing<!--end____isolette_gumbox-unit-testing-setup-title-->
<!--start__isolette_gumbox-unit-testing-setup-description-->
<!--end____isolette_gumbox-unit-testing-setup-description-->
### <!--start__isolette_gumbox-unit-testing-setup_configurations-title-->Unit Test Run Configurations<!--end____isolette_gumbox-unit-testing-setup_configurations-title-->
<!--start__isolette_gumbox-unit-testing-setup_configurations-description-->
<!--end____isolette_gumbox-unit-testing-setup_configurations-description-->
#### <!--start__isolette_gumbox-unit-testing-setup_configurations_ma_configurations-title-->MA<!--end____isolette_gumbox-unit-testing-setup_configurations_ma_configurations-title-->
<!--start__isolette_gumbox-unit-testing-setup_configurations_ma_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/isolette/Monitor/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_GumboX_UnitTests.scala), that was subsequently modified to provide custom configurations
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/isolette/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_DSC_UnitTests/report.html).  Custom configurations were used for this component. Click [here](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/default_configs/isolette/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_DSC_UnitTests/report.html)
for the coverage report obtained when only the default configurations are used

    - Configurations for the Initialize Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Monitor/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_UnitTestConfiguration_Util.scala#L22">Default_Initialize_Config</a></td><td>Default Initialize Configuration</td></tr>
      </table>

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/bridge/isolette/Monitor/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_GumboX_UnitTests.scala#L69">Compute_Config_ranges_based_on_requirements</a></td><td>Ranges based on requirements - ie 96.0 <= lower_alarm <= 101.0 and 97.0 <= upper_alarm <= 102.  Also restrict current_temp to be between 86.0 and 112.0 inclusive</td></tr>
      </table>

<!--end____isolette_gumbox-unit-testing-setup_configurations_ma_configurations-description-->

#### <!--start__isolette_gumbox-unit-testing-setup_configurations_mmi_configurations-title-->MMI<!--end____isolette_gumbox-unit-testing-setup_configurations_mmi_configurations-title-->
<!--start__isolette_gumbox-unit-testing-setup_configurations_mmi_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/isolette/Monitor/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/isolette/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface_DSC_UnitTests/report.html)

    - Configurations for the Initialize Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Monitor/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface_UnitTestConfiguration_Util.scala#L22">Default_Initialize_Config</a></td><td>Default Initialize Configuration</td></tr>
      </table>

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Monitor/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface_UnitTestConfiguration_Util.scala#L38">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Monitor/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface_UnitTestConfiguration_Util.scala#L61">Default_ComputewL_Config</a></td><td>Default ComputewL Configuration</td></tr>
      </table>

<!--end____isolette_gumbox-unit-testing-setup_configurations_mmi_configurations-description-->

#### <!--start__isolette_gumbox-unit-testing-setup_configurations_mmm_configurations-title-->MMM<!--end____isolette_gumbox-unit-testing-setup_configurations_mmm_configurations-title-->
<!--start__isolette_gumbox-unit-testing-setup_configurations_mmm_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/isolette/Monitor/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/isolette/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode_DSC_UnitTests/report.html)

    - Configurations for the Initialize Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Monitor/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode_UnitTestConfiguration_Util.scala#L22">Default_Initialize_Config</a></td><td>Default Initialize Configuration</td></tr>
      </table>

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Monitor/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode_UnitTestConfiguration_Util.scala#L38">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Monitor/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode_UnitTestConfiguration_Util.scala#L60">Default_ComputewL_Config</a></td><td>Default ComputewL Configuration</td></tr>
      </table>

<!--end____isolette_gumbox-unit-testing-setup_configurations_mmm_configurations-description-->

#### <!--start__isolette_gumbox-unit-testing-setup_configurations_mhs_configurations-title-->MHS<!--end____isolette_gumbox-unit-testing-setup_configurations_mhs_configurations-title-->
<!--start__isolette_gumbox-unit-testing-setup_configurations_mhs_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/isolette/Regulate/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/isolette/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_DSC_UnitTests/report.html)

    - Configurations for the Initialize Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Regulate/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_UnitTestConfiguration_Util.scala#L22">Default_Initialize_Config</a></td><td>Default Initialize Configuration</td></tr>
      </table>

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Regulate/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_UnitTestConfiguration_Util.scala#L38">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Regulate/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_UnitTestConfiguration_Util.scala#L61">Default_ComputewL_Config</a></td><td>Default ComputewL Configuration</td></tr>
      </table>

<!--end____isolette_gumbox-unit-testing-setup_configurations_mhs_configurations-description-->

#### <!--start__isolette_gumbox-unit-testing-setup_configurations_mri_configurations-title-->MRI<!--end____isolette_gumbox-unit-testing-setup_configurations_mri_configurations-title-->
<!--start__isolette_gumbox-unit-testing-setup_configurations_mri_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/isolette/Regulate/Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/isolette/Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface_DSC_UnitTests/report.html)

    - Configurations for the Initialize Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Regulate/Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface_UnitTestConfiguration_Util.scala#L22">Default_Initialize_Config</a></td><td>Default Initialize Configuration</td></tr>
      </table>

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Regulate/Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface_UnitTestConfiguration_Util.scala#L38">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
      </table>

<!--end____isolette_gumbox-unit-testing-setup_configurations_mri_configurations-description-->

#### <!--start__isolette_gumbox-unit-testing-setup_configurations_mrm_configurations-title-->MRM<!--end____isolette_gumbox-unit-testing-setup_configurations_mrm_configurations-title-->
<!--start__isolette_gumbox-unit-testing-setup_configurations_mrm_configurations-description-->
- Auto-generated GUMBOX Unit Test Harness [link](hamr/slang/src/test/bridge/isolette/Regulate/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode_GumboX_UnitTests.scala)
- Auto-generated component coverage report using the configurations below [link](https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs/isolette/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode_DSC_UnitTests/report.html)

    - Configurations for the Initialize Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Regulate/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode_UnitTestConfiguration_Util.scala#L22">Default_Initialize_Config</a></td><td>Default Initialize Configuration</td></tr>
      </table>

    - Configurations for the Compute Entrypoint
      <table>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Regulate/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode_UnitTestConfiguration_Util.scala#L38">Default_Compute_Config</a></td><td>Default Compute Configuration</td></tr>
        <tr><td valign=top><a href="hamr/slang/src/test/util/isolette/Regulate/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode_UnitTestConfiguration_Util.scala#L60">Default_ComputewL_Config</a></td><td>Default ComputewL Configuration</td></tr>
      </table>

<!--end____isolette_gumbox-unit-testing-setup_configurations_mrm_configurations-description-->
