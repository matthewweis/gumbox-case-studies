// #Sireum

package report

import org.sireum._
import org.sireum.hamr.codegen.common.symbols.SymbolTable
import org.sireum.hamr.codegen.common.util.CodeGenConfig
import org.sireum.message.Reporter
import report.Report

object ReadmeGen extends App {

  val runCodegen: B = F
  val replaceReadmes: B = T
  val emitMarkdown: B = T

  val repoRootDir: Os.Path = {
    val c = Os.path(".").up.up.up
    if (!(c/ "isolette").exists || !(c / "rts").exists || ~(c / "temp_control" / "periodic").exists) {
      halt(s"Root dir should contain all the subprojects: $c")
    }
    c
  }

  val rootDefaultConfigCoverageLink: String = "https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/default_configs"
  val rootCustomConfigCoverageLink: String = "https://people.cs.ksu.edu/~santos_jenkins/pub/gumbox-journal/custom_configs"

  @enum object EntrypointType {
    "Initialize"
    "Compute"
  }

  @datatype class UnitTestConfig(val name: String,
                                 val description: String,
                                 val entrypointType: EntrypointType.Type)

  @datatype class ComponentArtifacts(val componentNickName: String,
                                     val componentFullName: String,
                                     val testConfigs: ISZ[UnitTestConfig],
                                     val manualTestingFilename: Os.Path,
                                     val dscTestingFileName: Os.Path,
                                     val defaultConfigLocation: Os.Path
                                    ) {
    def simpleManualTestSuiteName: String = {
      return ops.StringOps(manualTestingFilename.name).replaceAllLiterally(".scala", "")
    }
    def simpleDscHarnessName: String = {
      return ops.StringOps(dscTestingFileName.name).replaceAllLiterally(".scala", "")
    }
  }

  @datatype class Project(val title: String,
                          val coverageRootName: String,
                          val description: Option[String],
                          val projectRootDir: Os.Path,
                          //val aadlRootDir: Os.Path,
                          val air: Os.Path,
                          //val packageName: String,

                          // NOTE: ignore tipe warning that it can't find Cli.SireumHamrCodegenOption
                          //       as the source for that is in the jitpack jar file and thus not
                          //       accessible to tipe
                          val configs: ISZ[Cli.SireumHamrCodegenOption],

                          val testComponentArtifacts: ISZ[ComponentArtifacts]
                         )


  val isolette: Project = {
    val projRootDir = repoRootDir / "isolette"
    val defaultDirs = Util.getDefaultDirectories(projRootDir)

    Project(
      title = "Isolette",
      coverageRootName = "isolette",
      description = None(),
      projectRootDir = projRootDir,
      air = defaultDirs.json,
      configs = ISZ(Util.baseOptions(
        packageName = Some("isolette"),
        args = ISZ(defaultDirs.json.value),
        outputDir = Some(defaultDirs.slangDir.value),
        aadlRootDir = Some(defaultDirs.aadlDir.value)
      )),
      testComponentArtifacts = getTestArtifacts(defaultDirs.slangDir / "src" / "test"/ "bridge" / "isolette", defaultDirs.slangDir)
    )
  }

  val rts: Project = {
    val projRootDir = repoRootDir / "rts"
    val defaultDirs = Util.getDefaultDirectories(projRootDir)

    Project(
      title = "RTS",
      coverageRootName = "rts",
      description = None(),
      projectRootDir = projRootDir,
      air = defaultDirs.json,
      configs = ISZ(Util.baseOptions(
        packageName = Some("RTS"),
        args = ISZ(defaultDirs.json.value),
        outputDir = Some(defaultDirs.slangDir.value),
        aadlRootDir = Some(defaultDirs.aadlDir.value)
      )),
      testComponentArtifacts = getTestArtifacts(defaultDirs.slangDir / "src" / "test" / "bridge" / "RTS", defaultDirs.slangDir)
    )
  }

  val tempControlPeriodic: Project = {
    val projRootDir = repoRootDir / "temp_control" / "periodic"
    val defaultDirs = Util.getDefaultDirectories(projRootDir)

    Project(
      title ="Temperature Control Sporadic",
      coverageRootName = "tc",
      description = None(),
      projectRootDir = projRootDir,
      air = defaultDirs.json,
      configs = ISZ(Util.baseOptions(
        packageName = Some("tc"),
        args = ISZ(defaultDirs.json.value),
        outputDir = Some(defaultDirs.slangDir.value),
        aadlRootDir = Some(defaultDirs.aadlDir.value)
      )),
      testComponentArtifacts = getTestArtifacts(defaultDirs.slangDir / "src" / "test" / "bridge" / "tc", defaultDirs.slangDir)
    )
  }

  def h(s: String): Unit = {
    halt(s)
  }


  def getTestArtifacts(path: Os.Path, slangRoot: Os.Path): ISZ[ComponentArtifacts] = {
    var artifacts: ISZ[ComponentArtifacts] = ISZ()
    for (json <- Os.Path.walk(path, T, F, p => p.ext == "json")) {
      var name = ops.StringOps(json.name).replaceAllLiterally("_DSC_UnitTests.scala.json", "")
      name = ops.StringOps(name).substring(1, name.size)
      val rel = path.relativize(json)
      artifacts = artifacts :+ ComponentArtifacts(
        componentNickName = getNickName(name),
        componentFullName = getComponentFullName(name),

        manualTestingFilename = json.up / s"${name}_GumboX_UnitTests.scala",
        dscTestingFileName =  json.up / s"${name}_DSC_UnitTests.scala",
        defaultConfigLocation = Util.hackyFind(slangRoot / "src" / "test" / "util", s"${name}_UnitTestConfiguration_Util.scala").get,
        testConfigs = getConfigs(json)
      )
    }
    return artifacts
  }

  def getComponentFullName(name: String): String = {
    assert (Util.threadNicknames.contains(name), name)
    return Util.threadNicknames.get(name).get
  }

  def getNickName(name: String): String = {
    assert (Util.threadNicknames.contains(name), name)
    return Util.threadNicknames.get(name).get
  }

  def getConfigs(json: Os.Path): ISZ[UnitTestConfig] = {
    assert (json.exists, json)
    val lines = json.readLines
    assert(lines.size == 1)
    val entries = ops.StringOps(ops.StringOps(lines(0)).substring(2, lines(0).size - 2)).split(c => c == ',')
    var configs: ISZ[UnitTestConfig] = ISZ()
    for (c <- entries) {
      val cc = ops.StringOps(ops.StringOps(c).trim)
      val x = ops.StringOps(cc.substring(1, cc.size - 1)).split(c => c == '|')
      val x0 = ops.StringOps(x(0))
      val etype: EntrypointType.Type = {
        if (x0.contains("Compute")) {EntrypointType.Compute}
        else if (x0.contains("Initialize")) {EntrypointType.Initialize}
        else {
          h(x(0))
          EntrypointType.Initialize
        }
      }
      configs = configs :+ UnitTestConfig(name = x(0), description = x(1), entrypointType = etype)
    }
    return configs
  }


  val projects: ISZ[Project] = ISZ(isolette, rts, tempControlPeriodic)

  def main(args: ISZ[String]): Z = {
    run()
    return 0
  }

  def run(): Unit = {
    val reporter = Reporter.create

    var reports: ISZ[Report] = ISZ()

    for(project <- projects) {

      val aadlRootDir = Os.path(project.configs(0).aadlRootDir.get)
      val packageName = project.configs(0).packageName.get

      assert(aadlRootDir.exists)

      for(config <- project.configs) {
        assert (config.aadlRootDir.get == aadlRootDir.value)
        assert (config.packageName.get == packageName)

        println("***************************************")
        println(s"${project.projectRootDir} -- ${config.platform})")
        println("***************************************")

        if (runCodegen) {
          org.sireum.cli.HAMR.codeGen(config, reporter)
        }

        reporter.printMessages()
      }

      if (!reporter.hasError) {
        val readmeLoc = project.projectRootDir / "readme.md"
        val readmeContent = Report.genReport(project, packageName, aadlRootDir, repoRootDir, reporter, emitMarkdown)
        if (!reporter.hasError) {
          if (!readmeLoc.exists || replaceReadmes) {
            Report.overwrite(readmeLoc, readmeContent)
          } else {
            Report.weave(readmeLoc, readmeContent)
          }
        }
      }
    }
  }
}
