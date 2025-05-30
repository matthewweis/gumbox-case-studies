// #Sireum

import org.sireum._
import Helper._

val removeDump: B = F
val regenMergedReports: B = F

val root = Os.path("/opt") / "santos" / "jenkins" / "dsc_sys" / "dsc_tester"
//val root = Os.path("/opt") / "santos" / "jenkins" / "dsc_sys" / "dsc_tester__BACKUP_14_08"

var results: ISZ[Results] = ISZ()
//                  proj             subsys           family           timeout
type hsm = HashSMap[String, HashSMap[String, HashSMap[String, HashSMap[Z, Results]]]]
var map: hsm = HashSMap.empty
Os.Path.walk(root, F, T, p => {
  if (p.name == "sireum.version") {
    val paths = p.up.list

    val testConfigP = fetch(".scala.json", p.up.up.list).get
    val scheduleInfoP = fetch(".scala_schedule.json", p.up.up.list).get
    val passingP = fetch(".passing", paths).get
    val failingP = fetch(".failing", paths).get
    val unsatP = fetch(".unsat", paths).get
    val execP = fetch(".exec", paths).get
    val csvP = fetch(".csv", paths).get
    val coverageP = fetch(".coverage", paths).get

    val testConfigs = parseJsonConfigs(testConfigP)
    val project = p.up.up.up.up.name
    val components: ISZ[String] =
      if (testConfigs.get("components").get.size == 0) coverageMap.get(project).get.keys
      else ops.StringOps(testConfigs.get("components").get).split(c => c == ',')
    val r = Results(
      project = project,
      subSystem = p.up.up.up.name,
      testFamily = p.up.up.name,
      timeout = Z(p.up.name).get,
      testConfiguration = testConfigs,
      testConfigurationP = testConfigP,
      scheduleInfo = parseJsonH(ISZ("nickNames", "scheduleProvider", "schedule"), scheduleInfoP.read),
      scheduleInfoP = scheduleInfoP,
      components = components,
      passing = numVectors(passingP) - numVectors(unsatP), // remove unsat from the passing vectors
      failing = numVectors(failingP),
      unsat = numVectors(unsatP),
      passingP = passingP,
      failingP = failingP,
      unsatP = unsatP,
      exec = execP,
      csv = csvP,
      coverage = coverageP
    )
    results = results :+ r
    map = add2Map(r, map)

    val dumpLoc = Helper.dumpLoc(r.project)

    fetch(".dump", paths) match {
      case Some(d) if removeDump && d.string != dumpLoc.string =>
        d.removeAll()
        println(s"Removed: dump ${d}")
      case Some(d) if removeDump =>
        println(s"Not removing ${d}")
      case _ =>
    }

  }
  F
})

var topLevelProjs: ISZ[(String, Os.Path)] = ISZ()
for (projects <- map.entries) {
  var projectResults = ISZ[Results]()
  for (systems <- projects._2.entries) {
    var systemResults = ISZ[Results]()
    for (families <- systems._2.entries) {
      var familyResults = ISZ[Results]()
      for (timeouts <- families._2.entries) {
        val results = timeouts._2
        familyResults = familyResults :+ results
        systemResults = systemResults :+ results
        projectResults = projectResults :+ results
        addTimeoutReport(results)
      }
      val familyRoot = familyResults(0).passingP.up.up
      val fResults: Results = mergeResults(familyResults, familyRoot)
      addConfigurationReport(fResults, families._2.keys, familyRoot)
    }
    val sysRoot = systemResults(0).passingP.up.up.up
    val sResults: Results = mergeResults(systemResults, sysRoot)
    addSystemReport(sResults, systems._2.keys, sysRoot)
  }
  val pRoot = projectResults(0).passingP.up.up.up.up
  val pResults: Results = mergeResults(projectResults, pRoot)
  addProjectReport(pResults, projects._2.keys, pRoot)

  topLevelProjs = topLevelProjs :+ (projects._1, pRoot)
}

addRootReport(root, topLevelProjs)


object Helper {

  def reportTemplate(cookieCrumbs: ST,
                     purpose: ST,
                     project: ST,
                     system: Option[ST],
                     families: Option[ST],
                     timeouts: Option[ST],
                     stats: Option[Results],
                     reportDir: Os.Path,
                     isActualRun: B,
                     coverage: Option[(ST, ISZ[ST])]): ST = {

    @strictpure def wrap(n: String, title: String, o: Option[ST]): Option[ST] =
      if (o.nonEmpty) Some(st"""<tr><td id=col_a title="$title">${n}: </td><td>${o.get}<br><br></td></tr>""")
      else None()

    val stats_ : Option[ST] = {
      stats match {
        case Some(s) =>
          def optLink(num: Z, p: Os.Path): ST = {
            return (
              if (isActualRun && num > 0) st"""<a href="${reportDir.relativize(p)}">$num</a> (click the link to view the serialized test vectors)"""
              else st"$num")
          }
          Some(
            st"""<tr>
                |  <td id=col_a title="Number of test vectors that passed">Passing:</td><td>${optLink(s.passing, s.passingP)}</td>
                |</tr><tr>
                |  <td id=col_a title="Number of test vectors that failed">Failing:</td><td>${optLink(s.failing, s.failingP)}</td>
                |</tr><tr>
                |  <td id=col_a title="Number of test vectors that failed to satisfy filter">Unsat:</td><td>${optLink(s.unsat, s.unsatP)}<br><br></td>
                |</tr>""")

        case _ => None()
      }
    }

    val coverage_ : Option[ST] =
      if (coverage.nonEmpty)
        Some(st"""<tr>
                 |  <td id=col_a title = "Combined code coverage information">Coverage:</td><td>Components Being Tested:<br><br>
                 |                                                                              ${(coverage.get._2, "<br>\n")}<br><br><br>
                 |
                 |                                                                             ${coverage.get._1}
                 |                                                                              </td>
                 |</tr>""")
      else None()

    return(
      st"""<html>
          |  <head>
          |    <style>
          |      td { vertical-align: top; }
          |      #col_a { font-weight: bold; }
          |    </style>
          |  </head>
          |  <body>
          |
          |<pre>
          |$cookieCrumbs
          |
          |<br>
          |$purpose
          |<br>
          |
          |<table>
          |  <tr><td id=col_a title="Project name">Projects:</td><td>$project<br><br></td></tr>
          |    ${wrap("Sub-Systems", "The sub-system(s) testing was run on", system)}
          |    ${wrap("Configurations", "The test configuration(s) used during testing", families)}
          |    ${wrap("Timeouts", "The timeout(s) used while generating test vectors", timeouts)}
          |    $stats_
          |    $coverage_
          |  </table>
          |</pre>
          |
          |  </body>
          |</html>
          |""")
  }

  def cookies(ps: ISZ[(String, Os.Path)]): ST = {
    val x = for(p <- ps) yield st"""<a href="${p._2}">${p._1}</a>"""
    return st"${(x, " > ")}"
  }

  def getCoverage(r: Results, reportDir: Os.Path): ISZ[ST] = {
    return r.components.map(c => {
      val e = coverageMap.get(r.project).get.get(c).get
      val summary = reportDir.relativize(r.coverage / s"${e}$$.html")
      val gumbox = r.coverage / s"${e}_GumboX$$.html"
      val gumboxR = reportDir.relativize(gumbox)
      val optGumbox =
        if (gumbox.exists) Some(st" <a href=$gumboxR>GumboX</a>")
        else None()
      st"""<a href=$summary>$c</a>$optGumbox"""
    })
  }

  def addTimeoutReport(r: Results): Unit = {
    val root1 = r.passingP.up

    val reportDir = root1

    val r0 = reportDir.relativize(root / "report.html")
    val r1 = reportDir.relativize(root / r.project / "report.html")
    val r2 = reportDir.relativize(root / r.project / r.subSystem / "report.html")
    val r3 = reportDir.relativize(root / r.project / r.subSystem / r.testFamily / "report.html")

    val cookieCrumb = cookies(ISZ(("Report", r0), (r.project, r1), (r.subSystem, r2), (r.testFamily, r3)))
    val subs: ISZ[ST] = getCoverage(r, reportDir)

    val html = reportTemplate(cookieCrumbs = cookieCrumb,
      purpose = st"""This presents the combined coverage information along with the number of passing/failing/unsat test vectors
                    |for the ${r.testFamily} configuration with a ${r.timeout} second timeout""",
      project = st"""<a href="$r1">${r.project}</a>""",
      system = Some(st"""<a href="$r2">${r.subSystem}</a>"""),
      families = Some(st"""<a href="$r3">${r.testFamily}</a> : ${r.testConfiguration.get("description").get}"""),
      timeouts = Some(st"""${r.timeout}"""),
      stats = Some(r),
      reportDir = reportDir,
      isActualRun = T,
      coverage = Some(st"""<a href="${reportDir.relativize(r.coverage)}/index.html">Full Report</a>""", subs)
    )
    val report = reportDir / "report.html"
    report.writeOver(html.render)
    println(s"Wrote: ${report}")
  }

  def addConfigurationReport(r: Results, timeouts: ISZ[Z], froot: Os.Path): Unit = {
    val reportDir = froot

    val r0 = reportDir.relativize(root / "report.html")
    val r1 = reportDir.relativize(root / r.project / "report.html")
    val r2 = reportDir.relativize(root / r.project / r.subSystem / "report.html")

    val stimeouts = ops.ISZOps(timeouts).sortWith((a,b) => a <= b)
    val timeOuts = for(t <- stimeouts) yield s"<a href=\"${(reportDir.relativize(reportDir / t.string / "report.html")).string}\">${t.string}</a>"

    val cookieCrumb = cookies(ISZ(("Report", r0), (r.project, r1), (r.subSystem, r2)))
    val subs: ISZ[ST] = getCoverage(r, reportDir)

    val html = reportTemplate(cookieCrumbs = cookieCrumb,
      purpose = st"""This presents the combined coverage information along with the number of passing/failing/unsat test vectors
                    |for the ${r.testFamily} configuration using timeouts of 1, 5, and 10 seconds""",
      project = st"""<a href="$r1">${r.project}</a>""",
      system = Some(st"""<a href="$r2">${r.subSystem}</a>"""),
      families = Some(st"""${r.testFamily} : ${r.testConfiguration.get("description").get}"""),
      timeouts = Some(st"""${(timeOuts, " ")}"""),
      stats = Some(r),
      reportDir = reportDir,
      isActualRun = F,
      coverage = Some(st"""<a href="${reportDir.relativize(r.coverage)}/index.html">Full Report</a>""", subs)
    )
    val report = reportDir / "report.html"
    report.writeOver(html.render)
    println(s"Wrote: ${report}")
  }

  def addSystemReport(r: Results, families: ISZ[String], sroot: Os.Path): Unit = {
    val reportDir = sroot

    val r0 = reportDir.relativize(root / "report.html")
    val r1 = reportDir.relativize(root / r.project / "report.html")

    val famOuts = for(t <- families) yield s"""<a href="${(reportDir.relativize(reportDir / t / "report.html")).string}">${t.string}</a><br>"""

    val cookieCrumb = cookies(ISZ(("Report", r0), (r.project, r1)))
    //val subs = for (c <- coverageMap.get(r.project).get.entries) yield st"""<a href="${reportDir.relativize(r.coverage / c._2)}">${c._1}</a>"""
    val subs: ISZ[ST] = getCoverage(r, reportDir)

    val html = reportTemplate(
      cookieCrumbs = cookieCrumb,
      purpose = st"""The presents the combined coverage information along with the number of passing/failing/unsat test vectors
                     |for running all the configurations for ${r.subSystem} using timeouts of 1, 5, and 10 seconds""",
      project = st"""<a href="$r1">${r.project}</a>""",
      system = Some(st"${r.subSystem}"),
      families = Some(st"${(famOuts, " ")}"),
      timeouts= None(),
      stats = Some(r),
      reportDir = reportDir,
      isActualRun = F,
      coverage = Some(st"""<a href="${reportDir.relativize(r.coverage)}/index.html">Full Report</a>""", subs)
    )

    val report = reportDir / "report.html"
    report.writeOver(html.render)
    println(s"Wrote: ${report}")
  }

  def addProjectReport(r: Results, systems: ISZ[String], sroot: Os.Path): Unit = {
    val reportDir = sroot

    val r0 = reportDir.relativize(root / "report.html")

    val sysOuts = for(sys <- systems) yield s"<a href=\"${(reportDir.relativize(reportDir / sys / "report.html")).string}\">${sys.string}</a><br><br>"

    val cookieCrumb = cookies(ISZ(("Report", r0)))
    //val subs = for (c <- coverageMap.get(r.project).get.entries) yield st"""<a href="${reportDir.relativize(r.coverage / c._2)}">${c._1}</a>"""
    val subs: ISZ[ST] = getCoverage(r, reportDir)

    val html = reportTemplate(
      cookieCrumbs = cookieCrumb,
      purpose = st"""This presents the combined coverage information along with the number of passing/failing/unsat test vectors
                    |for each of the ${r.project}'s subsystems under testing using timeouts of 1, 5, and 10 seconds""",
      project = st"${r.project}",
      system = Some(st"${(sysOuts, " ")}"),
      families = None(),
      timeouts = None(),
      stats = Some(r),
      reportDir = reportDir,
      isActualRun = F,
      coverage = Some(st"""<a href="${reportDir.relativize(r.coverage)}/index.html">Full Report</a>""", subs)
    )
    val report = reportDir / "report.html"
    report.writeOver(html.render)
    println(s"Wrote: ${report}")
  }

  def addRootReport(root: Os.Path, topLevelProjs: ISZ[(String, Os.Path)]): Unit = {
    val projects = for (p <- topLevelProjs) yield st"""<a href="${root.relativize(p._2 / "report.html")}">${p._1}</a>"""
    val html = reportTemplate(
      cookieCrumbs = st"",
      purpose = st"",
      project = st"${(projects, "<br><br>")}",
      system = None(),
      families = None(),
      timeouts = None(),
      stats = None(),
      reportDir = root,
      F,
      coverage = None()
    )
    val f = root / "report.html"
    f.writeOver(html.render)
    println(s"Wrote: $f")
  }


  def jarLoc(project: String): Os.Path = {
    project match {
      case string"isolette" => Os.home / "devel" / "git" / "hamr-system-testing-case-studies" / "isolette/hamr/slang/out/slang/assemble/slang.jar"
      case string"RTS" => Os.home / "devel" / "git" / "hamr-system-testing-case-studies" / "rts/hamr/slang/out/slang/assemble/slang.jar"
      case _ => halt(s"Unexpected ${project}")
    }
  }

  def dumpLoc(project: String): Os.Path = {
    project match {
      case string"isolette" => Os.path("/opt/santos/jenkins/dsc_sys/dsc_tester/isolette/Monitor_Subsystem_DSC_Test_Harness/MA__Failing__CT____Alarm_On/1/isolette_Monitor_Subsystem_DSC_Test_Harness_MA__Failing__CT____Alarm_On_1_mac-mini-m1-jacoco.dump")
      case string"RTS" => Os.path("/opt/santos/jenkins/dsc_sys/dsc_tester/RTS/Actuation_Subsystem_DSC_Test_Harness/ALU_Satisfies_Oracle/1/RTS_Actuation_Subsystem_DSC_Test_Harness_ALU_Satisfies_Oracle_1_mac-mini-m1-jacoco.dump")
      case _ => halt(s"Unexpected ${project}")
    }
  }

  val coverageMap: Map[String, HashSMap[String, String]] = {
    var m: Map[String, HashSMap[String, String]] = Map.empty
    m = m + "isolette" ~> (HashSMap.empty[String, String] ++ ISZ(
      ("DMF", "isolette.Monitor/Detect_Monitor_Failure_impl_thermostat_monitor_temperature_detect_monitor_failure"),
      ("MonMA", "isolette.Monitor/Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm"),
      ("MonMMI", "isolette.Monitor/Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface"),
      ("MonMMM", "isolette.Monitor/Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode"),

      ("DRF", "isolette.Regulate/Detect_Regulator_Failure_impl_thermostat_regulate_temperature_detect_regulator_failure"),
      ("RegMHS", "isolette.Regulate/Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source"),
      ("RegMRI", "isolette.Regulate/Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface"),
      ("RegMRM", "isolette.Regulate/Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode")
    ))
    m = m + "RTS" ~> (HashSMap.empty[String, String] ++ ISZ(
      // Actuation Unit 1 threads
      ("au1_temp_coincidenceLogic", "RTS.Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_temperatureLogic_coincidenceLogic"),
      ("au1_press_coincidenceLogic","RTS.Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_pressureLogic_coincidenceLogic"),
      ("au1_satLogic_coincidenceLogic", "RTS.Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit1_saturationLogic_coincidenceLogic"),
      ("au1_tempPressTripOut_orLogic", "RTS.Actuation/OrLogic_i_actuationSubsystem_actuationUnit1_tempPressureTripOut_orLogic"),

      // Actuation Unit 2 threads
      ("au2_temp_coincidenceLogic", "RTS.Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_temperatureLogic_coincidenceLogic"),
      ("au2_press_coincidenceLogic", "RTS.Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_pressureLogic_coincidenceLogic"),
      ("au2_sat_coincidenceLogic", "RTS.Actuation/CoincidenceLogic_i_actuationSubsystem_actuationUnit2_saturationLogic_coincidenceLogic"),
      ("au2_tempPressTripOut_orLogic", "RTS.Actuation/OrLogic_i_actuationSubsystem_actuationUnit2_tempPressureTripOut_orLogic"),

      // Temperature/Pressure actuation
      ("TPAU_actTempPA_orLogic","RTS.Actuation/OrLogic_i_actuationSubsystem_tempPressureActuatorUnit_actuateTempPressureActuator_orLogic"),
      ("TPAU_tempPressA_actuator", "RTS.Actuation/Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator"),

      // Saturation actuation
      ("SAU_actSatActuator_orLogic", "RTS.Actuation/OrLogic_i_actuationSubsystem_saturationActuatorUnit_actuateSaturationActuator_orLogic"),
      ("SAU_satActuator_actuator", "RTS.Actuation/Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator"),
    ))
    m
  }

  //                                        isolette        mon               famil            time
  def add2Map(r: Results, projMap: HashSMap[String, HashSMap[String, HashSMap[String, HashSMap[Z, Results]]]]): hsm = {
    var sysMap: HashSMap[String, HashSMap[String, HashSMap[Z, Results]]] = projMap.get(r.project) match {
      case Some(sysmap) => sysmap
      case _ => HashSMap.empty[String, HashSMap[String, HashSMap[Z, Results]]]()
    }
    val famMap: HashSMap[String, HashSMap[Z, Results]] = sysMap.get(r.subSystem) match {
      case Some(projmap) => projmap
      case _ => HashSMap.empty[String, HashSMap[Z, Results]]()
    }
    val timMap: HashSMap[Z, Results] = famMap.get(r.testFamily) match {
      case Some(timmap) => timmap
      case _ => HashSMap.empty[Z, Results]()
    }
    assert(!timMap.contains(r.timeout))

    return projMap + r.project ~> (sysMap + (r.subSystem ~> (famMap + r.testFamily ~> (timMap + (r.timeout ~> r)))))

  }

  def parseJsonConfigs(p: Os.Path): HashSMap[String, String] = {
    val keys: ISZ[String] = ISZ("testConfigurationName", "description", "schema", "property", "profile", "filter", "components")
    return parseJsonH(keys, p.read)
  }

  def parseJsonH(keys: ISZ[String], str: String): HashSMap[String, String] = {
    val p = org.sireum.Json.Parser.create(str)
    p.parseObjectBegin()
    var entries = HashSMap.empty[String, String]()
    do {
      val key = p.parseObjectKeys(keys)
      val value = p.parseString()
      entries = entries + key ~> value
    } while (p.parseObjectNext())

    return entries
  }

  @datatype class Results(project: String,
                          subSystem: String,
                          testFamily: String,
                          timeout: Z,

                          val testConfiguration:  HashSMap[String, String],
                          val testConfigurationP: Os.Path,

                          val scheduleInfo: HashSMap[String, String],
                          val scheduleInfoP: Os.Path,

                          val components: ISZ[String],

                          val passing: Z,
                          val failing: Z,
                          val unsat: Z,

                          val passingP: Os.Path,
                          val failingP: Os.Path,
                          val unsatP: Os.Path,

                          val exec: Os.Path,
                          val csv: Os.Path,
                          val coverage: Os.Path)

  def fetch(suffix: String, paths: ISZ[Os.Path]): Option[Os.Path] = {
    return (ops.ISZOps(paths).filter(f => ops.StringOps(f.name).endsWith(suffix)) match {
      case ISZ(e) => Some(e)
      case _ => None()
    })
  }

  def numVectors(p: Os.Path): Z = {
    return ops.StringOps(p.read).split(c => c == '\n').size
  }

  def getComponents(results: Helper.Results): ISZ[String] = {
    return ops.StringOps(results.testConfiguration.get("components").get).split(c => c == ',')
  }

  def mergeResults(results: ISZ[Results], relativeTo: Os.Path): Results = {
    var (passing, failing, unsat): (Z, Z, Z) = (0, 0, 0)
    var components: Set[String] = Set.empty
    var execsPaths: ISZ[Os.Path] = ISZ()
    for (r <- results) {
      passing = passing + r.passing
      failing = failing + r.failing
      unsat = unsat + r.unsat
      execsPaths = execsPaths :+ r.exec
      components = components ++ r.components
    }

    val jacocoOutDir = relativeTo / "jacocoCoverage"

    if (regenMergedReports) {
      val jacocoCli = Os.home / "devel" / "sireum" / "kekinian" / "lib" / "jacococli.jar"
      //val projJar = jarLoc(results(0).project)
      val projDump = dumpLoc(results(0).project)

      val csv = relativeTo / "jacoco.csv"

      val sireumHome = Sireum.homeOpt.get
      val javaExe = Sireum.javaHomeOpt.get / "bin" / (if (Os.isWin) "java.exe" else "java")

      val execs = for(x <- execsPaths) yield x.string

      println(s"Working on ${relativeTo.string} ...")
      val commands = ISZ[String](javaExe.string, "-jar", jacocoCli.string, "report") ++ execs ++ ISZ[String]("--encoding",
        "UTF-8", "--classfiles", projDump.string, "--csv", csv.string, "--html", jacocoOutDir.string, "--sourcefiles", projDump.string)

      //println(commands)
      Os.proc(commands).console.echo.runCheck()
      println()
    }

    val x = results(0)(passing = passing, failing = failing, unsat = unsat, coverage = jacocoOutDir, components = components.elements)

    return x
  }
}
