// #Sireum

package report

import org.sireum._

object Util {
  def threadNickNameBySuffix(suffix: String): String = {
    for (key <- threadNicknames.keys if (ops.StringOps(key).endsWith(suffix))) {
      return threadNicknames.get(key).get
    }
    halt(s"Couldn't find suffix ${suffix}")
  }
  val threadNicknames: Map[String, String] = Map.empty ++ ISZ(
    "Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator" ~> "SAU_satActuator_actuator",
    "Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator" ~> "TPAU_tempPressA_actuator",

    "CoincidenceLogic_i_actuationSubsystem_actuationUnit1_pressureLogic_coincidenceLogic" ~> "au1_press_coincidenceLogic",
    "CoincidenceLogic_i_actuationSubsystem_actuationUnit1_saturationLogic_coincidenceLogic" ~> "au1_satLogic_coincidenceLogic",
    "CoincidenceLogic_i_actuationSubsystem_actuationUnit1_temperatureLogic_coincidenceLogic" ~> "au1_temp_coincidenceLogic",

    "CoincidenceLogic_i_actuationSubsystem_actuationUnit2_pressureLogic_coincidenceLogic" ~> "au2_press_coincidenceLogic",
    "CoincidenceLogic_i_actuationSubsystem_actuationUnit2_saturationLogic_coincidenceLogic" ~> "au2_sat_coincidenceLogic",
    "CoincidenceLogic_i_actuationSubsystem_actuationUnit2_temperatureLogic_coincidenceLogic" ~> "au2_temp_coincidenceLogic",

    "OrLogic_i_actuationSubsystem_actuationUnit1_tempPressureTripOut_orLogic" ~> "au1_tempPressTripOut_orLogic",
    "OrLogic_i_actuationSubsystem_actuationUnit2_tempPressureTripOut_orLogic" ~> "au2_tempPressTripOut_orLogic",

    "OrLogic_i_actuationSubsystem_saturationActuatorUnit_actuateSaturationActuator_orLogic" ~> "SAU_actSatActuator_orLogic",
    "OrLogic_i_actuationSubsystem_tempPressureActuatorUnit_actuateTempPressureActuator_orLogic" ~> "TPAU_actTempPA_orLogic",

    "InstrumentationMockThread_i_instrumentationMock_instrumentationMockThread" ~> "instrumentationMockThread",
    "EventControlMockThread_i_eventControlMock_eventControlMockThread" ~> "eventControlMockThread",
    "ActuatorsMockThread_i_actuatorsMock_actuatorsMockThread" ~> "actuatorsMockThread",


    "operator_interface_thread_impl_operator_interface_oip_oit" ~> "OpInterface",

    "Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source" ~> "MHS",
    "Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface" ~> "MRI",
    "Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode" ~> "MRM",

    "Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface" ~> "MMI",
    "Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm" ~> "MA",
    "Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode" ~> "MMM",

    "Heat_Source_impl_heat_source_cpi_heat_controller" ~> "HS",
    "Detect_Regulator_Failure_impl_thermostat_regulate_temperature_detect_regulator_failure" ~> "DRF",
    "Detect_Monitor_Failure_impl_thermostat_monitor_temperature_detect_monitor_failure" ~> "DMF",
    "Temperature_Sensor_impl_temperature_sensor_cpi_thermostat" ~> "TS",


    "FanPeriodic_p_tcproc_fan" ~> "Fan",
    "OperatorInterfacePeriodic_p_tcproc_operatorInterface" ~> "OpInterface",
    "TempControlPeriodic_p_tcproc_tempControl" ~> "TempControl",
    "TempSensorPeriodic_p_tcproc_tempSensor" ~> "TempSensor"
  )

  def hackyFind(dir: Os.Path, suffix: String): Option[Os.Path] = {
    assert(dir.isDir, s"${dir}")
    for(f <- dir.list if f.isFile) {
      if (ops.StringOps(f.name).endsWith(suffix)) {
        return Some(f)
      }
    }
    for(subdir <- dir.list if subdir.isDir) {
      hackyFind(subdir, suffix) match {
        case Some(matchy) => return Some(matchy)
        case _ =>
      }
    }
    return None()
  }

  def readLines(s: String, sep: C): ISZ[String] = {
    // sireum's split does not preserve blank lines so a space.
    // TODO drop the space
    return ops.StringOps(ops.StringOps(s).replaceAllLiterally(s"$sep", s" $sep")).split(c => c == sep)
  }

  def parseJson(str: String, keys: ISZ[String]): HashSMap[String, String] = {
    val p = org.sireum.Json.Parser.create(str)
    p.parseObjectBegin()
    var entries = HashSMap.empty[String, String]()
    do {
      val key = p.parseObjectKeys(keys)
      val value = p.parseString()
      entries = entries + key ~> value
    } while(p.parseObjectNext())
    return entries
  }

  @datatype class Dirs(val rootDir: Os.Path,
                       val aadlDir: Os.Path,
                       val slangDir: Os.Path,
                       val json: Os.Path)
  def getDefaultDirectories(projRootDir: Os.Path): Dirs = {
    def exists(path: Os.Path): Os.Path = {
      if (!path.exists) {
        halt(s"${path} does not exist")
      }
      return path
    }
    def getJson(path: Os.Path): Os.Path = {
      val cands = Os.Path.walk(path, T, F, p => p.ext == "json")
      if (cands.size != 1) {
        halt(s"Found ${cands.size} json files in ${path}")
      }
      return cands(0)
    }
    return Dirs(
      rootDir = projRootDir,
      aadlDir = exists(projRootDir / "aadl"),
      slangDir = exists((projRootDir / "hamr" / "slang")),
      json = getJson(projRootDir / "aadl")
    )
  }


  val baseOptions: Cli.SireumHamrCodegenOption = Cli.SireumHamrCodegenOption(
    help = "",
    args = ISZ(),
    msgpack = F,
    verbose = T,
    runtimeMonitoring = T,
    platform = Cli.SireumHamrCodegenHamrPlatform.JVM,
    packageName = None(),

    noProyekIve = T,
    noEmbedArt = F,
    devicesAsThreads = F,
    excludeComponentImpl = F,
    genSbtMill = F,

    outputDir = None(),
    slangAuxCodeDirs = ISZ(),
    slangOutputCDir = None(),
    camkesOutputDir = None(),
    camkesAuxCodeDirs = ISZ(),
    aadlRootDir = None(),

    bitWidth = 32,
    maxStringSize = 256,
    maxArraySize = 1,
    runTranspiler = F,

    experimentalOptions = ISZ()
  )

  def locateText(s: String, lines: ISZ[String], linkPrefix: Os.Path): Option[String] = {
    return locateTextD(F, F, s, lines, linkPrefix)
  }
  def locateTextD(rev: B, makeHtmlLinks: B, s: String, lines: ISZ[String], linkPrefix: Os.Path): Option[String] = {
    if (ops.StringOps(s).startsWith("Default ")) {
      return (if (makeHtmlLinks) Some("getDefaultProfile, <i>i.e. uses default configurations as provided by SlangCheck</i>")
       else Some("getDefaultProfile, _i.e. uses default configurations as provided by SlangCheck_"))
    }
    if (rev) {
      for (i <- lines.size - 1 to 0 by -1 if ops.StringOps(lines(i)).contains(s)) {
        return Some(mkLink(makeHtmlLinks, s, s"${linkPrefix}#L${i + 1})"))
      }
    } else {
      for (i <- 0 until lines.size if ops.StringOps(lines(i)).contains(s)) {
        return Some(mkLink(makeHtmlLinks, s, s"${linkPrefix}#L${i + 1}"))
      }
    }
    return None()
  }
  def locateMethodDefinition(methodName: String, lines: ISZ[String], linkPrefix: Os.Path): String = {
    return locateMethodDefinitionH(F, methodName, lines, linkPrefix)
  }

  def mkLink(makeHtmlLinks: B, name: String, anchor: String): String = {
    return (
      if (makeHtmlLinks) s"<a href=\"$anchor\">$name</a>"
      else s"[$name]($anchor)"
    )
  }
  def locateMethodDefinitionH(makeHtmlLinks: B, methodName: String, lines: ISZ[String], linkPrefix: Os.Path): String = {
    if (methodName == "Regulate_Subsystem_Inputs_Container_GumboX.system_Pre_Container") {
      return mkLink(makeHtmlLinks, "system_Pre_Container", "hamr/slang/src/test/system/isolette/system_tests/rst/Regulate_Subsystem_Inputs_Container_GumboX.scala#L46")
    }
    if (methodName == "getDefaultProfile") {
      return "getDefaultProfile, _i.e. uses default configurations as provided by SlangCheck_"
    }
    val deffy = s"def $methodName"
    for (i <- 0 until lines.size if ops.StringOps(lines(i)).contains(deffy)) {
      return mkLink(makeHtmlLinks, methodName, s"${linkPrefix}#L${i+1}")
    }
    return s"Didn't find $methodName in $linkPrefix"
  }
}
