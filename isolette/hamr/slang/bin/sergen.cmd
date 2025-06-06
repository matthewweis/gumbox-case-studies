::/*#! 2> /dev/null                                   #
@ 2>/dev/null # 2>nul & echo off & goto BOF           #
if [ -z "${SIREUM_HOME}" ]; then                      #
  echo "Please set SIREUM_HOME env var"               #
  exit -1                                             #
fi                                                    #
exec "${SIREUM_HOME}/bin/sireum" slang run "$0" "$@"    #
:BOF
setlocal
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
"%SIREUM_HOME%\bin\sireum.bat" slang run %0 %*
exit /B %errorlevel%
::!#*/
// #Sireum

import org.sireum._

val sireum = Os.path(Os.env("SIREUM_HOME").get) / "bin" / (if (Os.isWin) "sireum.bat" else "sireum")

// Do not edit this file as it will be overwritten if HAMR codegen is rerun

// create serializers/deserializers for the Slang types used in the project

val files: ISZ[String] = ISZ("../src/main/data/isolette/Isolette_Environment/Heat.scala",
                             "../src/main/data/isolette/Isolette_Environment/Interface_Interaction.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/PhysicalTemp_impl.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/ValueStatus.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/TempWstatus_impl.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/On_Off.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/Status.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/Temp_impl.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/Regulator_Mode.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/Failure_Flag_impl.scala",
                             "../src/main/data/isolette/Isolette_Data_Model/Monitor_Mode.scala",
                             "../src/main/data/isolette/Base_Types.scala",
                             "../src/main/data/isolette/Regulate/Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface_Containers.scala",
                             "../src/main/data/isolette/Regulate/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source_Containers.scala",
                             "../src/main/data/isolette/Regulate/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode_Containers.scala",
                             "../src/main/data/isolette/Monitor/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface_Containers.scala",
                             "../src/main/data/isolette/Monitor/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm_Containers.scala",
                             "../src/main/data/isolette/Monitor/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode_Containers.scala",
                             "../src/main/data/isolette/util/Container.scala",
                             "../src/main/art/art/DataContent.scala",
                             "../src/main/data/isolette/Aux_Types.scala")

val toolargs: String = st"${(files, " ")}".render

(Os.slashDir.up / "src" / "main" / "util" / "isolette").mkdirAll()

proc"$sireum tools sergen -p isolette -m json,msgpack -o ${Os.slashDir.up}/src/main/util/isolette $toolargs".at(Os.slashDir).console.runCheck()
