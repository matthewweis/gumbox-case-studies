::/*#! 2> /dev/null                                 #
@ 2>/dev/null # 2>nul & echo off & goto BOF         #
if [ -z ${SIREUM_HOME} ]; then                      #
  echo "Please set SIREUM_HOME env var"             #
  exit -1                                           #
fi                                                  #
exec ${SIREUM_HOME}/bin/sireum slang run "$0" "$@"  #
:BOF
setlocal
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
%SIREUM_HOME%\\bin\\sireum.bat slang run "%0" %*
exit /B %errorlevel%
::!#*/
// #Sireum

import org.sireum._

val homeBin = Os.slashDir
val home = homeBin.up

val sireumBin = Os.path(Os.env("SIREUM_HOME").get) / "bin"
val sireum = sireumBin / (if(Os.isWin) "sireum.bat" else "sireum")

val dsc_prefix: String = "dsc_gumbox_journal_paper"
val jenkinsJobName: String = "0DSC_Unit_Testing_start"

@datatype class Container(val project: String,
                          val packageName: String,
                          val objectName: String,

                          val prettyName: String,
                          val tag: String
                         ) {
  def dscPrefix: String = {
    return s"$project-$tag"
  }
}

@datatype class TContainer(val node: String,
                           val server: String,
                           val basePackage: String,
                           val projRoot: Os.Path,
                           val timeouts: ISZ[Z],
                           val containers: ISZ[Container])

val isolette = "isolette" ~> TContainer(
  "mac-mini-m1",
  "e2206hm02.cs.ksu.edu",
  "isolette",
  home / "isolette" / "hamr" / "slang",
  ISZ(1, 5, 30, 60, 360),
  //ISZ(1, 5, 30),
  ISZ(
    Container("isolette", "isolette.Monitor", "Manage_Alarm_impl_thermostat_monitor_temperature_manage_alarm",
      "Manage Alarm", "ma"),
    Container("isolette", "isolette.Monitor", "Manage_Monitor_Interface_impl_thermostat_monitor_temperature_manage_monitor_interface",
      "Manage Monitor Interface", "mmi"),
    Container("isolette", "isolette.Monitor", "Manage_Monitor_Mode_impl_thermostat_monitor_temperature_manage_monitor_mode",
      "Manage Monitor Mode", "mmm"),
    Container("isolette", "isolette.Regulate", "Manage_Heat_Source_impl_thermostat_regulate_temperature_manage_heat_source",
      "Manage Heat Source", "mhs"),
    Container("isolette", "isolette.Regulate", "Manage_Regulator_Interface_impl_thermostat_regulate_temperature_manage_regulator_interface",
      "Manage Regulator Interface", "mri"),
    Container("isolette", "isolette.Regulate", "Manage_Regulator_Mode_impl_thermostat_regulate_temperature_manage_regulator_mode",
      "Manage Regulator Mode", "mrm")
  )
)

val rts = "rts" ~> TContainer(
  "mac-mini-intel",
  "e2206hm02.cs.ksu.edu",
  "RTS",
  home / "rts" / "hamr" / "slang",
  ISZ(1, 5, 30, 60, 360),
  ISZ(
    Container("rts", "RTS.Actuation", "Actuator_i_actuationSubsystem_saturationActuatorUnit_saturationActuator_actuator",
      "Saturation Actuator", "saturationActuator"),

    Container("rts", "RTS.Actuation", "Actuator_i_actuationSubsystem_tempPressureActuatorUnit_tempPressureActuator_actuator",
      "TempPressure Actuator Unit", "tempPressureActuator"),

    Container("rts", "RTS.Actuation", "CoincidenceLogic_i_actuationSubsystem_actuationUnit1_pressureLogic_coincidenceLogic",
      "Actuation Unit 1 Pressure Logic", "au1-pressureLogic"),
    Container("rts", "RTS.Actuation", "CoincidenceLogic_i_actuationSubsystem_actuationUnit1_saturationLogic_coincidenceLogic",
      "Actuation Unit 1 Saturation Logic", "au1-saturationLogic"),
    Container("rts", "RTS.Actuation", "CoincidenceLogic_i_actuationSubsystem_actuationUnit1_temperatureLogic_coincidenceLogic",
      "Actuation Unit 1 Temperature Logic", "au1-temperatureLogic"),


    Container("rts", "RTS.Actuation", "CoincidenceLogic_i_actuationSubsystem_actuationUnit2_pressureLogic_coincidenceLogic",
      "Actuation Unit 2 Pressure Logic", "au2-pressureLogic"),
    Container("rts", "RTS.Actuation", "CoincidenceLogic_i_actuationSubsystem_actuationUnit2_saturationLogic_coincidenceLogic",
      "Actuation Unit 2 Saturation Logic", "au2-saturationLogic"),
    Container("rts", "RTS.Actuation", "CoincidenceLogic_i_actuationSubsystem_actuationUnit2_temperatureLogic_coincidenceLogic",
      "Actuation Unit 2 Temperature Logic", "au2-temperatureLogic"),


    Container("rts", "RTS.Actuation", "OrLogic_i_actuationSubsystem_actuationUnit1_tempPressureTripOut_orLogic",
      "Actuation Unit 1 TempPressureTripOut or Logic", "au1-tempPressureTripOut"),
    Container("rts", "RTS.Actuation", "OrLogic_i_actuationSubsystem_actuationUnit2_tempPressureTripOut_orLogic",
      "Actuation Unit 2 TempPressureTripOut or Logic", "au2-tempPressureTripOut"),

    Container("rts", "RTS.Actuation", "OrLogic_i_actuationSubsystem_saturationActuatorUnit_actuateSaturationActuator_orLogic",
      "Actuate Saturation Actuator", "actuateSaturationActuator"),

    Container("rts", "RTS.Actuation", "OrLogic_i_actuationSubsystem_tempPressureActuatorUnit_actuateTempPressureActuator_orLogic",
      "Actuate TempPressure Actuator", "actuateTempPressureActuator")
  )
)

val tc = "tc" ~> TContainer(
  "mac-mini-m1",
  "e2206hm02.cs.ksu.edu",
  "tc",
  home / "temp_control" / "periodic" / "hamr" / "slang",
  ISZ(1, 5, 30, 60, 360),
  ISZ(
    Container("tc", "tc.CoolingFan", "FanPeriodic_p_tcproc_fan",
      "Cooling Fan", "fan"),

    Container("tc", "tc.TempControlSoftwareSystem", "OperatorInterfacePeriodic_p_tcproc_operatorInterface",
      "Operator Interface", "operator-interface"),

    Container("tc", "tc.TempControlSoftwareSystem", "TempControlPeriodic_p_tcproc_tempControl",
      "Temperature Controller", "temp-control"),

    Container("tc", "tc.TempSensor", "TempSensorPeriodic_p_tcproc_tempSensor",
      "Temperature Sensor", "temp-sensor")
  )
)


val projects: Map[String, TContainer] = Map.empty[String, TContainer] ++ ISZ(isolette, rts, tc)


def getProj(): (String, TContainer) = {
  if (Os.cliArgs.size == 1) {
    println(s"Must specify a project: ${projects.keys}")
    Os.exit(0)
  }
  val proj: TContainer = projects.get(Os.cliArgs(1)) match {
    case Some(p) => p
    case x => halt(s"$x is not a valid project: ${projects.keys}")
  }
  return (Os.cliArgs(1), proj)
}

def getTimeout(tcontainer: TContainer): String = {
  if (Os.cliArgs.size < 3) {
    println(s"Must supply a timeout: ${tcontainer.timeouts}")
    Os.exit(0)
    halt("Infeasible")
  }
  else {
    val to = Os.cliArgs(2)
    if (!ops.ISZOps(tcontainer.timeouts).contains(Z(to).get)) {
      println(s"Must choose from: ${tcontainer.timeouts}")
      Os.exit(0)
      halt("Infeasible")
    }
    return to
  }
}

def rebuild: B = {
  if (Os.cliArgs.size == 4) {
    assert (Os.cliArgs(3) == "rebuild", "3rd argument is optional but must be 'rebuild'")
    return T
  }
  return F
}

def test(): Unit = {

  val (project, tcontainer) = getProj()

  val timeout = getTimeout(tcontainer)

  val jar = tcontainer.projRoot / "out" / project / "assemble" / s"${project}.jar"

  val jenkinsUser: String = Os.env("JENKINS_USER_ID") match {
    case Some(u) => u
    case _ => halt("Must set jenkins_user environment variable")
  }

  val jenkinsToken: String = Os.env("JENKINS_TOKEN") match {
    case Some(t) => t
    case _ => halt("Must set jenkins_token environment variable")
  }

  val slangCheckLoc = tcontainer.projRoot / "src" / "main" / "util" / tcontainer.basePackage / "SlangCheckRandom.scala"
  assert(slangCheckLoc.exists)

  val remoteJarLoc = s"${dsc_prefix}/${project}/${jar.name}"

  if(rebuild) {
    println("Replacing for loops with whiles ...")
    val lines = ops.StringOps(ops.StringOps(slangCheckLoc.read).replaceAllLiterally("\n", " \n")).split(c => c == '\n')
    val xlines = for (l <- lines) yield (if (ops.StringOps(l).contains("for (i <- 0 to get_Config") || ops.StringOps(l).contains("for(i <- 0 to get_Config")) "while(T) {" else l)
    slangCheckLoc.writeOver(st"${(xlines, "\n")}".render)

    println("Assembling ...")
    proc"sireum proyek assemble --include-sources --include-tests -n ${project} .".at(tcontainer.projRoot).console.runCheck()

    println("Reverting back to for loops ...")
    proc"git checkout ${slangCheckLoc.value}".at(tcontainer.projRoot).console.runCheck()

    def upload(server: String): Unit = {
      println(s"Uploading ${jar} to $server ...")
      proc"ssh santos_jenkins@${server} mkdir -p ${dsc_prefix}/${project}".console.runCheck()
      proc"scp ${jar.value} santos_jenkins@${server}:$remoteJarLoc".console.runCheck()
    }

    upload("linux.cs.ksu.edu")
    upload(tcontainer.server)
  } else {
    println("Not rebuilding/uploading since 'rebuild' cli option not set")
  }

  for (c <- tcontainer.containers) {
    val runnerSimpleName = s"${c.objectName}_DSC_UnitTests"
    val runner_class_name = s"${c.packageName}.$runnerSimpleName"
    val p = proc"java -cp $jar $runner_class_name".run()
    assert(p.ok)
    var copts = ops.StringOps(p.out)
    for (config <- ops.StringOps(copts.substring(2, p.out.size - 3)).split(c => c == ',')) {
      copts = ops.StringOps(ops.StringOps(config).trim)
      val configName = copts.substring(1, copts.indexOf('|'))

      val testerDir = s"/opt/santos/jenkins/$dsc_prefix/tester/$project/$runnerSimpleName/$configName/$timeout"
      val runnerDir = s"/opt/santos/jenkins/$dsc_prefix/runner/$project/$runnerSimpleName/$configName/$timeout"

      proc"ssh santos_jenkins@${tcontainer.server} mkdir -p ${runnerDir}".echo.runCheck()
      proc"ssh santos_jenkins@${tcontainer.server} mkdir -p ${testerDir}".echo.runCheck()

      val args = ISZ[String](
        s"DSC_RUNNER_CLASS_NAME=${runner_class_name}",
        s"DSC_RUNNER_SIMPLE_NAME=${c.dscPrefix}",
        s"DSC_TIMEOUT=${timeout}",
        s"DSC_CONFIG_NAME=$configName",
        s"DSC_PROJECT_NAME=${c.project}",
        s"DSC_JAR_LOC=$remoteJarLoc",
        s"DSC_PREFIX=$dsc_prefix",
        s"DSC_TEST_SERVER=${tcontainer.server}",
        s"DSC_RUNNER_DIR=$runnerDir",
        s"DSC_TESTER_DIR=$testerDir"
      )
      assert (ops.ISZOps(args).forall(z => !ops.StringOps(z).contains(" ")), s"data args cannot contain spaces: $args")

      val data = st"${(for (a <- args) yield s"--data $a", " ")}".render
      proc"curl  https://jenkins.cs.ksu.edu/job/${jenkinsJobName}/buildWithParameters --user ${jenkinsUser}:${jenkinsToken} $data".echo.runCheck()
    }
  }
}


def fetch(): Unit = {

  val (project, tcontainer) = getProj()

  val timeout = getTimeout(tcontainer)

  val timeoutDir = s"/opt/santos/jenkins/dsc-jacoco/${project}/timeout_${timeout}"
  val zipPrefix = s"${project}_timeout_${timeout}"

  for (c <- tcontainer.containers) {
    val dscPrefix = s"${timeoutDir}/${c.dscPrefix}-${tcontainer.node}-dsc-tested"
    def ffetch(ext: String): Z = {
      val temp = Os.temp()
      proc"scp santos_jenkins@${tcontainer.server}:${dscPrefix}.${ext} ${temp.value}".console.runCheck()
      return temp.readLines.size
    }
    val passing = ffetch("passing")
    val failing = ffetch("failing")
    val unsat = ffetch("unsat")
    val ttemp = Os.temp()
    ttemp.writeOver(s"${passing-unsat}\n${failing}\n${unsat}\n")
    proc"scp ${ttemp.value} santos_jenkins@${tcontainer.server}:${dscPrefix}.results".console.runCheck()
  }

  val cmd = s"\"cd $timeoutDir && zip -r ${zipPrefix}.zip . -x '*dump*'\""
  proc"ssh santos_jenkins@${tcontainer.server} sh -c $cmd".echo.console.runCheck()
  println()
  println(s"Downloading via: scp santos_jenkins@${tcontainer.server}:${timeoutDir}/${zipPrefix}.zip ${zipPrefix}.zip")

  val resultsDir = s"public_html/dsc_results/${project}/results"
  proc"ssh santos_jenkins@linux.cs.ksu.edu rm -rf ${resultsDir}/${zipPrefix}".console.runCheck()
  proc"ssh santos_jenkins@linux.cs.ksu.edu mkdir -p ${resultsDir}".console.runCheck()
  proc"scp santos_jenkins@${tcontainer.server}:${timeoutDir}/${zipPrefix}.zip santos_jenkins@linux.cs.ksu.edu:${resultsDir}/${zipPrefix}.zip".echo.console.runCheck()
  proc"ssh santos_jenkins@linux.cs.ksu.edu unzip ${resultsDir}/${zipPrefix}.zip -d ${resultsDir}/${zipPrefix}".console.runCheck()
}

def report(): Unit = {
  for (e <- projects.entries) {
    println(s"Generating report for ${e._1}")
    var components: ISZ[ST] = ISZ()
    for (p <- e._2.containers) {
      var rows: ISZ[ST] = ISZ()
      for (t <- e._2.timeouts) {
        val timeoutPrefix = s"${p.project}_timeout_${t}"

        val directoryPrefix = s"results/${timeoutPrefix}/"

        val prefix = s"${p.dscPrefix}-${e._2.node}"

        val jPrefix = s"${prefix}-jacoco.coverage"

        val tr = Os.temp()
        val dscResults = s"${p.project}/${directoryPrefix}/${prefix}-dsc-tested.results"
        proc"scp santos_jenkins@linux.cs.ksu.edu:public_html/dsc_results/$dscResults ${tr.value}".console.runCheck()
        val trl = tr.readLines
        val passing = Z(trl(0)).get
        val failing = Z(trl(1)).get
        val unsat = Z(trl(2)).get
        val total = passing + failing + unsat

        val passingLink = s"${directoryPrefix}/${prefix}-dsc-tested.passing"
        val failingLink = s"${directoryPrefix}/${prefix}-dsc-tested.failing"
        val unsatLink = s"${directoryPrefix}/${prefix}-dsc-tested.unsat"

        val csv = s"${directoryPrefix}/${jPrefix}.csv"
        val metrics = s"${directoryPrefix}/${jPrefix}/${p.packageName}/index.source.html"
        val ccov = s"${directoryPrefix}/${jPrefix}/${p.packageName}/${p.objectName}.scala.html"
        val gcov = s"${directoryPrefix}/${jPrefix}/${p.packageName}/${p.objectName}_GumboX.scala.html"

        rows = rows :+
          st"""<tr>
              |  <td>${t}</td>
              |  <td>$total</td>
              |  <td><a href="$passingLink">$passing</a></td>
              |  <td><a href="$failingLink">$failing</a></td>
              |  <td><a href="$unsatLink">$unsat</a></td>
              |  <td><a href="$metrics">link</a></td>
              |  <td><a href="$ccov">link</a></td>
              |  <td><a href="$gcov">link</a></td>
              |  <td><a href="$csv">csv</a></td>
              |  <td><a href="results/${timeoutPrefix}.zip">zip</a></td>
              |</tr>"""
      }

      components = components :+
        st"""<h2>${p.prettyName}</h2>
            |<table border=1>
            |  <tr>
            |    <th>Timeout (s)</th>
            |    <th>Total<br>Tests</th>
            |    <th>Passing<br>Tests</th>
            |    <th>Failing<br>Tests</th>
            |    <th>Unsat<br>Requires</th>
            |    <th>Package<br>Coverage</th>
            |    <th>Component<br>Coverage</th>
            |    <th>GumboX<br>Coverage</th>
            |    <th>csv<br>Coverage</th>
            |    <th>Zipped<br>Results</th>
            |  </tr>
            |  ${(rows, "\n")}
            |</table>"""
    }

    val projWebsite =
      st"""<html>
          |  <style>
          |    a { text-decoration: none; }
          |    table {
          |      border: collapse;
          |      table-layout: fixed;
          |    }
          |    th {
          |      background-color: #E5E4E2;
          |      padding-left: 5px;
          |      padding-right: 5px;
          |    }
          |    td {
          |      padding-left: 5px;
          |      padding-right: 5px;
          |    }
          |    tr:nth-child(odd) { background-color: #f2f2f2; }
          |  </style>
          |  <body>
          |     ${(components, "\n")}
          |  </body>
          |</html>"""

    val f = Os.temp()
    f.writeOver(projWebsite.render)

    proc"scp ${f.value} santos_jenkins@linux.cs.ksu.edu:public_html/dsc_results/${e._1}/${e._1}.html".console.runCheck()
  }

  val website =
    st"""<html>
        |  <body>
        |    ${(for (k <- projects.keys) yield s"<a href=\"${k}/${k}.html\">${k}</a>", "\n\n<p>")}
        |  </body>
        |</html>"""

  val f = Os.temp()
  f.writeOver(website.render)

  proc"scp ${f.value} santos_jenkins@linux.cs.ksu.edu:public_html/dsc_results/index.html".console.runCheck()
}

if (Os.cliArgs.isEmpty) {
  println("Choose an option (test | fetch | report)")
  Os.exit(0)
}

Os.cliArgs(0) match {
  case string"test" => test()
  case string"fetch" => fetch()
  case string"report" => report()
  case _ => halt("Not a valid option")
}