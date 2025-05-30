// #Sireum
package report

import org.sireum._
import org.sireum.hamr.codegen.common.StringUtil
import org.sireum.hamr.codegen.common.symbols.{AadlComponent, AadlThread, SymbolTable}
import org.sireum.hamr.ir
import org.sireum.message.{Position, Reporter}
import report.ReadmeGen.{EntrypointType, Project, projects, repoRootDir}
import report.Report._

@sig trait Level {
  def tag: String
}

@datatype class ReportBlock(val tag: String,
                            val content: Option[ST]) extends Level

@datatype class ReportLevel(val tag: String,
                            val title: Option[ST],
                            val description: Option[ST],
                            val content: ISZ[ReportBlock],
                            val subLevels: ISZ[ReportLevel]) extends Level

object Report {
  val htmlDir: String = "https://people.cs.ksu.edu/~santos_jenkins/pub/hamr-system-testing-case-studies"

  def wrapWithTag(tag: String, isTitle: B, content: Option[ST]): ST = {
    assert (!seenTags.contains(tag), s"$tag already exists")
    seenTags = seenTags + tag
    val (start, end): (String, String) = (s"<!--start__${tag}-->", s"<!--end____${tag}-->")
    val ret: ST = {
      if (isTitle) st"${start}${content}${end}"
      else
        st"""${start}
            |${content}
            |${end}"""
    }
    return ret
  }

  @strictpure def getCookies(cookies: ISZ[Level]): ST = if(cookies.isEmpty) st"" else st"${(for (c <- cookies) yield c.tag, "_")}_"
  @strictpure def getBlockTag(block: ReportBlock, cookies: ISZ[Level]): String = st"${getCookies(cookies)}${block.tag}".render
  @strictpure def getTitleTag(reportLevel: ReportLevel, cookies: ISZ[Level]): String = st"${getCookies(cookies)}${reportLevel.tag}-title".render

  @strictpure def getDescTag(reportLevel: ReportLevel, cookies: ISZ[Level]): String = st"${getCookies(cookies)}${reportLevel.tag}-description".render

  def overwrite(readmeLoc: Os.Path, report: ReportLevel): Unit = {
    def renderLevel(l: ReportLevel, pos: Z, cookies: ISZ[Level]): ST = {
      val hashes = st"${(for (i <- 0 to pos) yield "#", "")}"

      def renderBlock(block: ReportBlock): ST = {
        wrapWithTag(getBlockTag(block, cookies :+ l), F, block.content)
      }

      return (
        st"""$hashes ${wrapWithTag(getTitleTag(l, cookies), T, l.title)}
            |${wrapWithTag(getDescTag(l, cookies), F, l.description)}
            |${(for (b <- l.content) yield renderBlock(b), "\n")}
            |${(for (sl <- l.subLevels) yield renderLevel(sl, pos + 1, cookies :+ l), "\n")}""")
    }

    val content = renderLevel(report, 0, ISZ())
    readmeLoc.writeOver(content.render)
    println(s"Wrote: ${readmeLoc}")
  }


  def weave(readmeLoc: Os.Path, readmeContent: ReportLevel): Unit = {
    var existing = ops.StringOps(readmeLoc.read)

    def replaceContent(tag: String, content: ST, addNewLines: B): Unit = {
      val (start, end): (String, String) = (s"<!--start__${tag}-->", s"<!--end____${tag}-->")
      var posStart = existing.stringIndexOf(start)
      if (posStart > 0) {
        posStart = posStart + start.size // include the tag
        val posEnd = existing.stringIndexOfFrom(end, posStart)
        if (posEnd > 0) {
          val prelude: String = existing.substring(0, posStart)
          val postlude: String = existing.substring(posEnd, existing.size)
          val newContent: String =
            if (addNewLines)
              st"""$prelude
                  |$content
                  |$postlude""".render
            else st"$prelude$content$postlude".render
          existing = ops.StringOps(newContent)
        } else {
          cprintln(T, s"didn't find end tag ${end}")
        }
      } else {
        cprintln(T, s"didn't find start tag ${start}")
      }
    }

    def walk(level: Level, cookies: ISZ[Level]): Unit = {
      level match {
        case r: ReportLevel =>
          if (r.title.nonEmpty) {
            replaceContent(getTitleTag(r, cookies), r.title.get, F)
          }
          if (r.description.nonEmpty) {
            replaceContent(getDescTag(r, cookies), r.description.get, T)
          }
          for (rb <- r.content) {
            replaceContent(getBlockTag(rb, cookies :+ level), rb.content.get, T)
          }
          for (sl <- r.subLevels) {
            walk(sl, cookies :+ level)
          }
        case x => halt("Infeasible")
      }
    }
    walk(readmeContent, ISZ())

    readmeLoc.writeOver(existing.s)
    println(s"Replaced: ${readmeLoc}")
  }

  var seenTags: Set[String] = Set.empty

  def genReport(project: Project, packageName: String, aadlRootDir: Os.Path, rootDir: Os.Path, reporter: Reporter, emitMarkdown: B): ReportLevel = {
    seenTags = Set.empty
    return Report(packageName, aadlRootDir, repoRootDir, emitMarkdown).genReport(project)
  }

  def createAadlComponentLink(linkToImplementation: B,
                              component: org.sireum.hamr.ir.Component,
                              isRoot: B,
                              aadlDir: Os.Path,
                              rootDir: Os.Path): ST = {
    val componentType: String = component.category match {
      case ir.ComponentCategory.System => "system"
      case ir.ComponentCategory.Process => "process"
      case ir.ComponentCategory.Processor => "processor"
      case ir.ComponentCategory.Thread => "thread"
      case _ => "??"
    }
    val classifier = component.classifier.get
    val name = ops.StringOps(ops.StringOps(classifier.name).replaceAllLiterally("::", ":")).split(c => c == ':')
    val fileCands = Os.Path.walk(aadlDir, T, F,
      p =>
        p.isFile && p.ext == "aadl" &&
          ops.ISZOps(p.readLines).exists(l => ops.StringOps(l).trim == s"package ${name(0)}")
    )
    assert(fileCands.size == 1)

    val file = fileCands(0)
    val (searchStr, textToDisplay): (String, String) = {
      val nameSplit = ops.StringOps(name(1)).split(c => c == '.')
      if (nameSplit.size == 1) (s"$componentType ${name(1)}", name(1)) // no period
      else if (linkToImplementation) (s"$componentType implementation ${name(1)}", name(1))
      else (s"$componentType ${nameSplit(0)}", nameSplit(0))
    }

    val declPos = component.identifier.pos.get
    val lines = file.readLines
    for (i <- 0 until lines.size if ops.StringOps(lines(i)).contains(searchStr) && (isRoot || declPos.beginLine != i + 1)) {
      return st"[${name(0)}::${textToDisplay}](${rootDir.relativize(file).value}#L${i + 1})"
    }
    halt(s"Infeasible: couldn't find $searchStr in $file")
  }

  def sortThreads(threads: ISZ[AadlThread], symbolTable: SymbolTable): ISZ[AadlThread] = {
    return (
      ops.ISZOps(threads()).sortWith((a, b) => a.component.classifier.get.name < b.component.classifier.get.name))
  }

  def createHyperLink(title: String, target: String): ST = {
    return st"[${title}](${target})"
  }

  def createTag(s: String): String = {
    val lc = StringUtil.toLowerCase(s)
    val d = StringUtil.replaceAll(lc, " ", "-")
    val cis = conversions.String.toCis(d)

    // only keep numbers, lowercase letters, '-' and '_'
    val cis_ = cis.filter(c =>
      (c.value >= 48 && c.value <= 57) || (c.value >= 97 && c.value <= 122) ||
        (c == '-') || (c == '_'))
    val d_ = conversions.String.fromCis(cis_)
    return d_
  }

  def toGithubLink(s: String): String = {
    return s"#${createTag(s)}"
  }

  def splitClassifier(c: AadlComponent): (String, String) = {
    val o = ops.StringOps(ops.StringOps(c.component.classifier.get.name).replaceAllLiterally("::", ":")).split(c => c == ':')
    return (o(0), o(1))
  }

  def sanitize(s: String): String = {
    return ops.StringOps(s).replaceAllLiterally(".", "_")
  }

  def findMethod(key: String, f: Os.Path): Z = {
    assert(f.isFile, s"$f is not a file")
    assert(!ops.StringOps(f.read).contains("\r"), s"$f contains windows style new lines")

    var line = 1
    // add space before newline as split does not preserve empty lines (i.e. those that only contain newline char)
    for (l <- ops.StringOps(ops.StringOps(f.read).replaceAllLiterally("\n", " \n")).split(c => c == '\n')) {
      if (ops.StringOps(l).contains(s"def $key(api: ")) {
        return line
      }
      line = line + 1
    }
    halt(s"Infeasible, didn't find $key in $f")
  }
}

@datatype class Report(packageName: String, aadlRootDir: Os.Path, repoRoot: Os.Path, emitMarkDown: B) {

  def runCloc(dirs: ISZ[Os.Path]): ST = {

    // define a language processing filter for camkes
    val camkesClocDef =
      st"""CAmkES
          |    filter rm_comments_in_strings " /* */
          |    filter rm_comments_in_strings " //
          |    filter call_regexp_common C++
          |    extension camkes
          |    3rd_gen_scale 2.00
          |    end_of_line_continuation \\$$"""
    val temp = Os.temp()
    temp.writeOver(camkesClocDef.render)

    val args: ISZ[String] = ISZ[String](
      "cloc",
      "--md",
      "--exclude-lang=make",
      s"--read-lang-def=${temp.value}"
    ) ++ dirs.map((m: Os.Path) => m.value)

    val results = Os.proc(args).runCheck()
    val s = ops.StringOps(results.out)

    val ret = st"${(ops.ISZOps(s.split(c => c == '\n')).drop(2), "\n")}"

    return ret
  }

  def dirsScanned(project: Project, dirs: ISZ[Os.Path]): ST = {
    val rdirs = dirs.map((m: Os.Path) => project.projectRootDir.relativize(m)).map((m: Os.Path) => st"- [${m}]($m)")

    val _dirs: ST =
      st"""Directories Scanned Using [https://github.com/AlDanial/cloc](https://github.com/AlDanial/cloc) v1.94:
          |${(rdirs, "\n")}"""
    return _dirs
  }

  def genAadlMetrics(project: Project): ReportLevel = {

    assert(project.configs.size == 1)
    val model = AadlModelUtil.getModel(project.air, F)
    val symbolTable = AadlModelUtil.getSymbolTable(model, project.configs(0).packageName.get, project.configs(0))

    val threads = symbolTable.getThreads()

    val connections = symbolTable.connections

    var ports: Z = 0
    for (t <- threads) {
      ports = ports + t.getPorts().size
    }

    val ret: ST =
      st"""| | |
           ||:--|:--|
           ||Threads|${threads.size}|
           ||Ports|${ports}|
           ||Connections|${connections.size}|"""

    return ReportLevel(
      tag = createTag("AADL Metrics"),
      title = Some(st"AADL Metrics"),
      description = Some(
        st"""The following section provides statistics about the AADL model to give a rough idea of
            |its size (in terms of number of AADL modeling elements that impact the size of the deployed system)."""),
      content = ISZ(ReportBlock(
        tag = createTag("AADL Metrics content block"),
        content = Some(ret))),
      subLevels = ISZ()
    )
  }

  def processUserCloc(root: Os.Path): ST = {
    var code: Z = 0
    var log: Z = 0

    def process(d: Os.Path): Unit = {
      if (d.isDir) {
        for (f <- d.list) {
          process(f)
        }
      } else {
        if (d.ext == "loc") {
          val props = d.properties
          code = code + Z(props.getOrElse("code", "0")).get
          log = log + Z(props.getOrElse("log", "0")).get
        }
      }
    }

    process(root)

    val ret: ST =
      st"""|Type|code |
           ||:--|--:|
           ||Behavior|${(code - log)}|
           ||Log|${log}|
           ||--------|--------|
           ||SUM:|${code}|"""

    return ret
  }

  def genJVMMetrics(project: Project): ReportLevel = {

    assert(project.configs.size == 1)

    val mainDir = Os.path(project.configs(0).outputDir.get) / "src" / "main"
    assert(mainDir.exists && mainDir.isDir)

    val cloc: ST = runCloc(ISZ(mainDir))

    val _dirsScanned = dirsScanned(project, ISZ(mainDir))

    val userCloc: ST = processUserCloc(mainDir)

    val ret: ST =
      st"""${_dirsScanned}
          |
          |<u><b>Total LOC</b></u>
          |
          |Total number of HAMR-generated and developer-written lines of code
          |${cloc}
          |
          |<u><b>User LOC</b></u>
          |
          |The number of lines of code written by the developer.
          |"Log" are lines of code used for logging that
          |likely would be excluded in a release build
          | ${userCloc}"""

    val block = ReportBlock(
      tag = createTag(s"${project.title}_code_metrics"),
      content = Some(ret))

    return ReportLevel(
      tag = createTag("JVM Metrics"),
      title = Some(st"JVM Metrics"),
      description = Some(st"The following section provides statistics about the Slang source code."),
      content = ISZ(block),
      subLevels = ISZ()
    )
  }

  def genCodeMetrics(project: Project): ReportLevel = {

    return ReportLevel(
      tag = createTag("Metrics"),
      title = Some(st"Metrics"),
      description = None(),
      content = ISZ(),
      subLevels = ISZ(genAadlMetrics(project), genJVMMetrics(project))
    )
  }

  def processNickNames(names: String, table: SymbolTable): Map[String, AadlComponent] = {
    var ret: Map[String, AadlComponent] = Map.empty
    for(n <- ops.StringOps(names).split(c => c == ',')) {
      val split = ops.StringOps(n).split(c => c == ':')
      val compCand = table.componentMap.values.filter(p => p.pathAsString("_") == split(1))
      assert (compCand.size == 1, compCand.size)
      ret = ret + split(0) ~> compCand(0)
    }
    return ret
  }

  def genTestingSection(project: Project, table: SymbolTable): ReportLevel = {

    val componentReports: ISZ[ReportLevel] = {
      var componentReports: ISZ[ReportLevel] = ISZ()
      for (componentArtifact <- project.testComponentArtifacts) {

        var entries: HashSMap[EntrypointType.Type, ISZ[ST]] = HashSMap.empty
        val manualTestingFile = componentArtifact.manualTestingFilename
        val mtfContents = ops.StringOps(ops.StringOps(manualTestingFile.read).replaceAllLiterally("\n", " \n")).split(c => c == '\n')
        val mtf = project.projectRootDir.relativize(manualTestingFile)

        val dscHarnessFile = componentArtifact.dscTestingFileName
        assert(dscHarnessFile.exists, dscHarnessFile)

        for (unitConfig <- componentArtifact.testConfigs) {
          val configName = unitConfig.name
          val configDescription = unitConfig.description
          val entrypoint = unitConfig.entrypointType

          var x = Util.locateTextD(
            rev = F,
            makeHtmlLinks = T,
            s = configName,
            lines = Util.readLines(componentArtifact.manualTestingFilename.read, '\n'),
            linkPrefix = project.projectRootDir.relativize(componentArtifact.manualTestingFilename)
          )
          if (x.isEmpty) {
            x = Util.locateTextD(
              rev = F,
              makeHtmlLinks = T,
              s = configName,
              lines = Util.readLines(componentArtifact.defaultConfigLocation.read, '\n'),
              linkPrefix = project.projectRootDir.relativize(componentArtifact.defaultConfigLocation)
            )
          }

          val t = st"<tr><td valign=top>${x.get}</td><td>${configDescription}</td></tr>"

          val isz: ISZ[ST] = if (entries.contains(entrypoint)) entries.get(entrypoint).get else ISZ()
          entries = entries + (entrypoint ~> (isz :+ t))
        }

        //var ret: ISZ[ReportBlock] = ISZ()
        var ret: ISZ[ST] = ISZ()
        for (e <- entries.entries) {
          val content = st"""- Configurations for the ${e._1} Entrypoint
                            |  <table>
                            |    ${(e._2, "\n")}
                            |  </table>
                            |"""
          //ret = ret :+ ReportBlock(
           // tag = createTag(s"${e._1}_configurations"),
            //content = Some(content))
          ret = ret :+ content
        }

        val optText: (Option[ST], Option[ST]) =
          if (componentArtifact.componentNickName == "MA" ||componentArtifact.componentNickName == "TempControl")
            (Some(st", that was subsequently modified to provide custom configurations"),
             Some(st""".  Custom configurations were used for this component. Click [here](${ReadmeGen.rootDefaultConfigCoverageLink}/${project.coverageRootName}/${componentArtifact.simpleDscHarnessName}/report.html)
                      |for the coverage report obtained when only the default configurations are used"""))
          else (None(), None())

        val coverageLink: String = s"[link](${ReadmeGen.rootCustomConfigCoverageLink}/${project.coverageRootName}/${componentArtifact.simpleDscHarnessName}/report.html)"
        val relly = project.projectRootDir.relativize(componentArtifact.manualTestingFilename)
        componentReports = componentReports :+
          ReportLevel(
            tag = createTag(s"${componentArtifact.componentFullName}_configurations"),
            title = Some(st"${componentArtifact.componentNickName}"),
            description = Some(
              st"""- Auto-generated GUMBOX Unit Test Harness [link](${relly})${optText._1}
                  |- Auto-generated component coverage report using the configurations below ${coverageLink}${optText._2}
                  |    
                  |    ${(ret, "\n")}"""),
            content = ISZ(),
            subLevels = ISZ()
          )
      }
      componentReports
    }

    val configurations: ReportLevel = ReportLevel(
      tag = createTag("configurations"),
      title = Some(st"Unit Test Run Configurations"),
      description = None(),
      content = ISZ(),
      subLevels = componentReports
    )

    val ret: ReportLevel = ReportLevel(
      tag = createTag("gumbox-unit-testing-setup"),
      title = Some(st"GUMBOX Unit Testing"),
      description = None(),
      content = ISZ(),
      subLevels = ISZ(configurations)
    )
    return ret
  }

  def findBehaviorCode(path: String, project: ReadmeGen.Project): (Option[Os.Path], Option[Os.Path]) = {
    val bridgesDir = project.projectRootDir / "hamr" / "slang" / "src" / "main" / "bridge"
    val componentsDir = project.projectRootDir / "hamr" / "slang" / "src" / "main" / "component"

    val behavior = Util.hackyFind(componentsDir, s"${path}.scala")
    val gumbox = Util.hackyFind(bridgesDir, s"${path}_GumboX.scala")

    return (behavior, gumbox)
  }

  def genHowToRunSection(project: Project, table: SymbolTable): ReportLevel = {

    val readmeRoot = project.projectRootDir
    val reportRoot = project.projectRootDir.up
    val stagReadme = readmeRoot.relativize(reportRoot / "report/util/SystemTestArtifactGen/readme.md")

    /*
    val exTestConfig = project.testConfigs(0)
    val exContainer = readmeRoot.relativize(exTestConfig.inputOutputContainers)
    val testOutputDirRel = readmeRoot.relativize(exTestConfig.systemTestOutputDir)
    val exManualRel = testOutputDirRel / s"${exTestConfig.exampleTestFrameworkPrefix}_Test_wSlangCheck.scala"
    val exDSCRel = testOutputDirRel / s"${exTestConfig.exampleTestFrameworkPrefix}_DSC_Test_Harness.scala"

    val framework: ReportLevel = ReportLevel(
      tag = createTag("framework-generation"),
      title = Some(st"Framework Generation"),
      description = Some(
        st"""1. Build the System Testing Artifact Generator following the instructions at
            |   [SystemTestArtifactGen/readme.md]($stagReadme)
            |1. Run the generator by passing it the paths to one or more files that contain
            |   input/output container definitions
            |
            |For example, running the generator on
            |[${exContainer.name}](${exContainer})
            |will generate the following artifacts:
            |
            |1. [${exManualRel.name}]($exManualRel)
            |
            |   System test suite containing an example test run configuration.  Developers can make a copy of this file and then define
            |   custom test run configurations where each configuration has the structure
            |   _(script schema, property, randomization profile, random vector filter)_
            |
            |1. [${exDSCRel.name}]($exDSCRel)
            |
            |      Example showing how a system test suite can be adapted for use with Distributed SlangCheck (DSC). It overrides/implements
            |   two DCS methods, ``next`` and ``test``.  The next method is called during DSC's test vector generation phase. The generated
            |   vectors are subsequently passed to the test method during DSC's testing phase. Both methods use the environment variable
            |   ``DSC_TEST_FAMILY_NAME`` to determine which test run configuration should be used.
            |
            |"""),
      content = ISZ(),
      subLevels = ISZ()
    )

    val subSystemsX: ISZ[String] = for (sub <- project.testConfigs) yield sub.systemName
    val subSystems: String = if (subSystemsX.size == 1) {
      subSystemsX(0)
    }
    else {
      val o = ops.ISZOps(subSystemsX)
      st"${(o.dropRight(1), ", ")} and ${o.last}".render
    }

    val manBlocks: ISZ[ReportBlock] = {
      var mbs: ISZ[ReportBlock] = ISZ()
      for (tconfig <- project.testConfigs) {
        val daconfig = tconfig.systemTestOutputDir / tconfig.manualTestingFilename
        val daconfigcontents = ops.StringOps(ops.StringOps(daconfig.read).replaceAllLiterally("\n", " \n")).split(c => c == '\n')
        val mtf = readmeRoot.relativize(daconfig)

        val testSuiteSuffix: String = ops.StringOps(tconfig.manualTestingFilename).replaceAllLiterally(".scala", "")
        val nest: ST =
          st"""System Test Suite Class: [${mtf.name}](${mtf})
              |
              |Test run configurations are specified via entries in the ${Util.locateText("testMatrix", daconfigcontents, mtf)}. For example,
              |the ${Util.locateText(tconfig.exampleTestConfig.name, daconfigcontents, mtf)} configuration uses the following:
              |
              || | |
              ||:--|--|
              || Script Schema: | ${Util.locateMethodDefinition(tconfig.exampleTestConfig.schema, daconfigcontents, mtf)}|
              || Property: | ${Util.locateMethodDefinition(tconfig.exampleTestConfig.property, daconfigcontents, mtf)}|
              || Randomization Profile: | ${Util.locateMethodDefinition(tconfig.exampleTestConfig.profile, daconfigcontents, mtf)}|
              || Random Vector Filter: | ${Util.locateMethodDefinition(tconfig.exampleTestConfig.filter, daconfigcontents, mtf)}|
              |
              |How to run:
              |
              |```
              |cd hamr-system-testing-case-studies
              |
              |sireum proyek test --suffixes ${testSuiteSuffix} ${project.projectRootDir.name}/hamr/slang
              |```
              |"""

        mbs = mbs :+ ReportBlock(
          tag = createTag(s"${tconfig.systemName}_block"),
          content = Some(
            st"""__${tconfig.systemName}__
                |
                |  $nest
                |""")
        )
      }
      mbs
    }

    val manualTesting: ReportLevel = ReportLevel(
      tag = createTag("manual-testing"),
      title = Some(st"Manual System Testing"),
      description = Some(
        st"""The example system test suites described previously were used to write
            |system tests for the ${subSystems} as follows:"""),
      content = manBlocks,
      subLevels = ISZ()
    )
    val bs = "\\"

    val title = project.title
    val pdir = project.projectRootDir.name
    val harness = project.testConfigs(0).simpleDscHarnessName
    val config = project.testConfigs(0).exampleTestConfig.name

    val phtmldir: String = {
      if (title == "Isolette") "isolette"
      else if (title == "RTS") "RTS"
      else "??"
    }

    val lt1 = s"$phtmldir/$harness/$config/1"
    val l1 = s"$htmlDir/$lt1"
    val lt2 = s"$phtmldir/$harness/$config"
    val l2 = s"$htmlDir/$lt2"
    val lt3 = s"$phtmldir/$harness"
    val l3 = s"$htmlDir/$lt3"
    val lt4 = s"$phtmldir"
    val l4 = s"$htmlDir/$lt4"

    val dscTesting: ReportLevel = ReportLevel(
      tag = createTag("dsc-testing"),
      title = Some(st"Distributed SlangCheck System Testing"),
      description = Some(
        st"""Background:
            |
            |System testing as put forth in this paper uses SlangCheck to generate input/injection test vectors.
            |SlangCheck is Sireum's randomized
            |test generator framework similar to ScalaCheck and Haskell's QuickCheck.
            |Distributed SlangCheck (DSC) adds a framework that allows test vector
            |generation to be run via a server cluster up to a user specified timeout. Increasing
            |the timeout allows more vectors to be produced which may lead to increased code
            |coverage during testing. DSC passes the vectors to user defined unit tests
            |and serializes the
            |passing and failing vectors to seperate files so that they can be replayed if needed.
            |DSC uses JaCoCo to produce code coverage information.
            |
            |Approach:
            |
            |The ${config} configuration of ${title}'s
            |${project.testConfigs(0).simpleManualTestSuiteName} test suite will be used to
            |illustrate how system testing can employ DSC.  The actual results reported in the next
            |section simply automated the following steps such that each configuration was run with timeouts
            |of 1, 5, and 10 seconds using a Jenkins cluster.
            |
            |Create a jar file for this project that includes the sources and tests suites
            |
            |```
            |cd hamr-system-testing-case-studies
            |
            |sireum proyek assemble --include-sources --include-tests ${pdir}/hamr/slang
            |```
            |
            |Set the environment variable ``DSC_TEST_FAMILY_NAME`` to indicate which configuration
            |should be used
            |
            |```
            |export DSC_TEST_FAMILY_NAME=${config}
            |```
            |
            |The following will repeatedly call ${harness}'s next method for 1 second to generate test vectors
            |and store them in a local file (DSC can be
            |configured to scp the results to a remote server where they can be combined with vectors
            |generated from other 'generating' servers)
            |```
            |sireum tools slangcheck runner$bs
            |  -t 1$bs
            |  -o $$(pwd)/${pdir}-dsc.jsons$bs
            |  -c ${pdir}/hamr/slang/out/slang/assemble/slang.jar$bs
            |  ${project.testConfigs(0).dscFQName}
            |```
            |
            |DSC is designed to only report passing and failing test vectors.  The generated DSC
            |test harness test methods extend this by invoking the configuration's random vector filter and
            |writing out unsat vectors to a file specified via the ``DSC_SAVE_LOC`` environment variable.
            |```
            |export DSC_SAVE_LOC=$$(pwd)/${pdir}-dsc-output.unsat
            |touch $$DSC_SAVE_LOC
            |```
            |
            |The following will pass each test vector to the ${harness}'s test method,
            |record the passing/failing/unsat test vectors in separate files and generate an HTML
            |report that combines the coverage information across all the runs.
            |```
            |sireum tools slangcheck tester$bs
            |  -i $$(pwd)/${pdir}-dsc.jsons.dsc.7z$bs
            |  -o $$(pwd)/${pdir}-dsc-output$bs
            |  --coverage $$(pwd)/${pdir}-jacoco$bs
            |  -c ${pdir}/hamr/slang/out/slang/assemble/slang.jar$bs
            |  --sourcepath ${pdir}/hamr/slang/out/slang/assemble/slang.jar$bs
            |  ${project.testConfigs(0).dscFQName}
            |```
            |
            |Results:
            |
            |The full experimental results for the ${title} are available at:<br>
            |[$l4]($l4/report.html)
            |
            |<br><br>
            |The following table explains the report directory structure,
            |starting with the results from a specific run of DSC and then walking
            |up the report directory hierarchy.
            |
            |||
            ||:--|
            ||[$lt1]($l1/report.html)<br><br>The combined coverage information along with the number of passing/failing/unsat test vectors for the ${config} configuration with a 1 second timeout<br><br>__NOTE__ this is what DSC was actually run on.  The following rows are simply aggregate information |
            ||[$lt2]($l2/report.html)<br><br>The combined coverage information along with the number of passing/failing/unsat test vectors for the MA__Failing__CT____Alarm_On configuration using timeouts of 1, 5, and 10 seconds |
            ||[$lt3]($l3/report.html)<br><br>The combined coverage information along with the number of passing/failing/unsat test vectors for running all the configurations through ${harness} using timeouts of 1, 5, and 10 seconds |
            ||[$lt4]($l4/report.html)<br><br>The combined coverage information along with the number of passing/failing/unsat test vectors for each of the ${title}'s subsystems under testing using timeouts of 1, 5, and 10 seconds |
            |"""),
      content = ISZ(),
      subLevels = ISZ()
    )
    */
    return ReportLevel(
      tag = createTag("how-to-run"),
      title = Some(st"How to Run"),
      description = Some(
        st"""GUMBOX unit testing requires a Sireum distribution. Instructions on how to obtain a
            |distribution are available at [https://sireum.org/getting-started/](https://sireum.org/getting-started/).
            |The rest of this documentation assumes the ``SIREUM_HOME`` environmental variable has been set and that
            |sireum's bin directory has been added to your path (e.g. for Linux/MacOS ``export PATH=$$SIREUM_HOME/bin:$$PATH``
            |or Windows ``set PATH=%PATH%\bin;%PATH%``
            |
            |"""),


      content = ISZ(),
      subLevels = ISZ()//framework, manualTesting, dscTesting)

    )
  }



  def collect(index: Z, content: ISZ[ISZ[String]]): Z = {
    var sum: Z = 0
    for (c <- content) {
      sum = sum + Z(c(index)).get
    }
    return sum / content.size
  }

  def allSame(index: Z, content: ISZ[ISZ[String]]): Unit = {
    var last: Z = -1
    for (c <- content) {
      if (last == -1) {
        last = Z(c(index)).get
      } else {
        assert(Z(c(index)).get == last)
      }
    }
  }

  def formatTime(s: Z): String = {
    val cis = conversions.String.toCis(s.string)
    var ret: ISZ[org.sireum.C] = ISZ()
    for (ix <- cis.size - 1 to 0 by -1) {
      ret = cis(ix) +: ret
      if (ret.size == 3) {
        ret = '.' +: ret
      }
    }
    val r: String = conversions.String.fromCis(ret)
    return if (ret.size == 2) s"0.0$r"
    else if (ret.size == 3) s"0.$r"
    else if (ret.size == 4) s"0$r"
    else r
  }

  def processResults(csv: Os.Path): HashSMap[String, ST] = {

    val rawlines = csv.readLines
    assert(rawlines(0) == "entrypoint,cliTime,logikaTime,processBegin,processCheck,vcsNum,vcsTime,satNum,satTime,timeStamp,kekikianBuild,timeout,rlimit,par,par-branch,par-branch-mode,System Version,Computer Name,Model Identifier,Processor,Memory")
    val lines: ISZ[ISZ[String]] = for (l <- ops.ISZOps(rawlines).drop(1)) yield ops.StringOps(l).split(c => c == ',')

    var entrypoints: Set[String] = Set.empty
    for (l <- lines) {
      entrypoints = entrypoints + l(0)
    }
    var ret = HashSMap.empty[String, ST]
    for (entryPoint <- entrypoints.elements) {
      val entries = ops.ISZOps(lines).filter(p => p(0) == entryPoint)
      val cliTime = collect(1, entries)
      val logikaTime = collect(2, entries)
      val processBegin = collect(3, entries)
      val processCheck = collect(4, entries)
      val vcsNum = collect(5, entries)
      allSame(5, entries)
      val vcsTime = collect(6, entries)
      val satNum = collect(7, entries)
      allSame(7, entries)
      val satTime = collect(8, entries)

      val ttime = formatTime(logikaTime)
      val itctime = formatTime(processBegin - processCheck)
      val vtime = formatTime(processCheck)


      ret = ret + entryPoint ~> st"|$vcsNum|$satNum|$ttime|$itctime|$vtime|"
    }
    return ret
  }

  // NOTE: ignore tipe warning that SymbolTable cannot be resolved as the source
  //       for that is in the jitpack jar file and thus not accessible to tipe
  def genLogikaSection(project: ReadmeGen.Project, table: SymbolTable): ReportLevel = {
    var subLevels: ISZ[ReportBlock] = ISZ()
    for (t <- table.getThreads()) {
      val cimplname: String = sanitize(splitClassifier(t)._2)
      val sp = st"${(ops.ISZOps(t.path).drop(1), "_")}".render
      val fnamePrefix: String = s"${cimplname}_${sp}"
      val scalaFilename = s"${fnamePrefix}.scala"

      val csvFilename = s".${scalaFilename}.csv"

      val componentDir = Os.path(project.configs(0).outputDir.get) / "src" / "main" / "component" / project.configs(0).packageName.get
      val csvFile = Os.Path.walk(componentDir, T, F, p => p.name == csvFilename)
      val scalaFile = Os.Path.walk(componentDir, T, F, p => p.name == scalaFilename)(0)

      if (csvFile.nonEmpty) {
        val tag = createTag(s"logika-${fnamePrefix}")
        var entries: ISZ[ST] = ISZ()
        for (r <- processResults(csvFile(0)).entries) {
          val lineNum = findMethod(r._1, scalaFile)
          val elink = st"[${r._1}](${project.projectRootDir.relativize(scalaFile)}#L${lineNum})"
          entries = entries :+ st"|${elink}${r._2}"
        }
        val table =
          st"""
              |**${t.identifier}**
              |
              |Raw Data: [csv](${project.projectRootDir.relativize(csvFile(0))})
              |
              |EntryPoint|VC|SAT|TTime|ITCTime|VTime|
              ||:--|:--|:--|:--|:--|:--|
              |${(entries, "\n")}"""

        subLevels = subLevels :+ ReportBlock(
          tag = tag,
          content = Some(table)
        ) /*
        subLevels = subLevels :+ Level(
          tag = tag,
          title = Some(st"${t.identifier}"),
          description = None(),
          content = ISZ(Block(s"${tag}-table", Some(table))),
          subLevels = ISZ()
        )*/
      }
    }

    val results = ReportLevel(
      tag = createTag("logiak-results"),
      title = Some(st"Results"),
      description = None(),
      content = subLevels,
      subLevels = ISZ()
    )
    val slashScript = repoRoot / "bin" / "report-logika.cmd"
    val relSlashScript = project.projectRootDir.relativize(slashScript)

    val howToRun = ReportLevel(
      tag = createTag("how-to-run"),
      title = Some(st"How to replicate"),
      description = Some(
        st"""To run the experiments, first install Sireum Kekinian (optionally choosing the
            |commit tip used for the experiments, ie. [843ede1](https://github.com/sireum/kekinian/tree/843ede1120e6e75fde089db0928ab66a3c9a3e73))
            |
            |```
            |git clone --rec https://github.com/sireum/kekinian.git
            |cd kekinian
            |git checkout 843ede1
            |git pull --rec
            |bin/build.cmd
            |
            |export SIREUM_HOME=$$(pwd)
            |export PATH=$$SIREUM_HOME/bin:$$PATH
            |```
            |
            |Then run the following Slash script specifying the number of number of times to rerun Logika
            |on each entrypoint: [${relSlashScript}](${relSlashScript})
            |
            |```
            |${relSlashScript} run 25
            |```
            |
            |The results will be appended to the csv file corresponding to the component
            |being evaluated. The line ``val projects: Map[String, Project] = Map.empty + isolette + rts + tcP + tcS``
            |in the script can be modified if you want to run a subset of the projects"""),
      content = ISZ(), subLevels = ISZ()
    )

    return ReportLevel(
      tag = createTag("logika"),
      title = Some(st"Logika"),
      description = Some(
        st"""The following reports the experimental data obtained by running Logika
            |only on the component entrypoints that require verification (e.g. TempControl's
            |Fan component was excluded as it does not contain GUMBO contracts and does not
            |use datatypes that have invariants).  Logika was configured with a 2 second
            |validity checking timeout, a 500 millisecond satisfiability checking timeout, a
            |SMT2 resource limit of 2,000,000, and with full parallelization optimizations
            |enabled.  The SMT2 solvers used include CVC4 1.8, CVC5 1.0.5, and Z3 4.12.2. The
            |**VC** and **SAT** columns report the number of verification and
            |satisfiability conditions that were checked, respectively.  The time values
            |reported in the final three columns are the averages obtained after re-running
            |Logika 25 times for each entrypoint on an M1 Mac Mini with 8 cores and 16 GB of
            |RAM.  **TTime** gives the total number of seconds it took to run Logika
            |from the command line on the Slang project containing the entrypoint (i.e. it
            |includes the verification time along with the time required for parsing, type
            |checking, etc.).
            |
            |One optimization technique related to using Logika from within IVE that can be
            |measured via our experimental setup is Sireum's incremental type checking. For
            |example, if Logika was run on the Isolette MA component's initialize entrypoint
            |from within IVE using an identical configuration as was done for the experiments
            |then it will take on average 2.482 seconds to verify, assuming Logika had not
            |previously been invoked.  If a change was then made to MA's source code before
            |re-running Logika on the timeTriggered entrypoint then Sireum's incremental type
            |checking will only need to recheck MA (and any of its dependents) resulting in
            |an average delay of only 0.214 seconds before verification can proceed. The
            |results of these optimizations are reported in the Incremental-Type Checking
            |column (**ITCTime**).  The time required to actually verify an entrypoint with
            |a clean cache is reported in the Verification-Time column (**VTime**) so
            |incremental type checking for this example would save 2.268 seconds (2.482 -
            |0.214) on average."""),
      content = ISZ(),
      subLevels = ISZ(results, howToRun)
    )
  }

  def genArchitectureSection(project: Project, table: SymbolTable): ReportLevel = {

    var blocks: ISZ[ReportBlock] = ISZ()

    var archDescription: ST = st""
    AadlModelUtil.getAadlArchDiagram(aadlRootDir) match {
      case Some(p) =>
        val rootDir = project.projectRootDir
        val rel = rootDir.relativize(p)
        val link = createHyperLink("AADL Arch", rel.value)
        //blocks = blocks :+ ReportBlock(
        //  tag = createTag("aadl-arch-diagram"),
        //  content = Some(st"!${link}"))
        archDescription =
          st"""!${link}
              |
              |The following documentation blocks provide links to AADL textual representation source of the Thread components in the system.
              |* "Type" links to the AADL component type declaration (providing the port-based interface for the component)
              |* "Behavior Specification" (when present) links to the GUMBO behavior contract for the component. HAMR automatically
              |compiles the GUMBO contract to both an code-level contract used for Logika code verification as well as an executable
              |representation of the contract (as pure boolean functions) used in unit and system testing."""
      case _ =>
        halt(s"Didn't find AADL arch diagram: ${project.title}")
    }

    val tagPrefix = "aadl-arch-component-info"

    val header: ST =
      createAadlComponentLink(T, table.rootSystem.component, T, aadlRootDir, project.projectRootDir)

    var systemProps =
      st"""|System: ${header}|
           ||:--|"""


    if (table.rootSystem.getUseRawConnection()) {
      systemProps =
        st"""${systemProps}
            ||Wire Protocol|"""
    }
    blocks = blocks :+ ReportBlock(
      tag = createTag(s"${tagPrefix}-${table.rootSystem.identifier}"),
      content = Some(systemProps))

    val threads: ISZ[AadlThread] = sortThreads(table.getThreads(), table)
    for (thread <- threads) {
      val name = thread.identifier
      val typ: String =
        if (thread.isPeriodic()) s"Periodic: ${thread.period.get} ms"
        else "Sporadic"

      val compType: Option[String] =
        if (thread.isCakeMLComponent()) Some("||CakeML|")
        else if (thread.getParent(table).toVirtualMachine(table)) Some("||Virtual Machine|")
        else None() //"Native"

      val domain: Option[ST] = if (thread.getParent(table).getDomain(table).nonEmpty) {
        Some(st"""|Domain: ${thread.getParent(table).getDomain(table).get}|""")
      } else {
        None()
      }

      val header: ST = {
        val pos: Position = thread.component.identifier.pos.get
        AadlModelUtil.createLinkFromPos(name, pos, aadlRootDir, project.projectRootDir)
      }

      val componentType = createAadlComponentLink(F, thread.component, F, aadlRootDir, project.projectRootDir)

      val componentImpl: Option[ST] =
        if (AadlModelUtil.isImplementation(thread)) {
          val link = createAadlComponentLink(T, thread.component, F, aadlRootDir, project.projectRootDir)
          Some(st"<br>Implementation: ${link}")
        }
        else {
          None()
        }

      val gumbo: Option[ST] = AadlModelUtil.getGumboSubclause(thread, table) match {
        case Some(g) =>
          val pos = AadlModelUtil.getGumboSubclausePos(g, aadlRootDir)
          Some(st"<br>Behavior Specification: ${AadlModelUtil.createLinkFromPos("GUMBO", pos, aadlRootDir, project.projectRootDir)}")
        case _ => None()
      }

      val tpath = st"${(ops.ISZOps(thread.path).drop(1), "_")}".render
      val tnick = Util.threadNickNameBySuffix(tpath)

      blocks = blocks :+ ReportBlock(
        tag = createTag(s"${tagPrefix}-${thread.identifier}-$tnick"),
        content = Some(
          st"""|Thread: $tnick <!--${header}--> |
               ||:--|
               ||Type: ${componentType}${componentImpl}${gumbo}|
               ${compType}
               ||${typ}|
               |${domain}
               |"""))
    }

    /*
      val proc: AadlProcessor = PeriodicUtil.getBoundProcessor(symbolTable)
      proc.getScheduleSourceText() match {
        case Some(loc) =>
          val schedule = Os.path(report.options.aadlRootDir.get) / loc
          assert(schedule.exists, schedule.canon)
          content =
            st"""$content
                |
                |**Schedule:** [${schedule.name}](${report.readmeDir.relativize(schedule).value})"""
        case _ =>
      }
    */

    return ReportLevel(
      tag = createTag("arch-section"),
      title = Some(st"AADL Architecture"),
      description = Some(archDescription),
      content = blocks,
      subLevels = ISZ()
    )
  }

  def genBehaviorCodeSection(project: ReadmeGen.Project, table: SymbolTable): ReportLevel = {
    var threads: ISZ[ReportBlock] = ISZ()

    for (thread <- table.getThreads()) {
      val tpath = st"${(ops.ISZOps(thread.path).drop(1), "_")}".render
      val tnick = Util.threadNickNameBySuffix(tpath)
      val (behavior, gumbox) = findBehaviorCode(tpath, project)
      val gumboxOpt: Option[ST] = if (gumbox.nonEmpty)
        Some(st"<br>[GumboX](${project.projectRootDir.relativize(gumbox.get)})")
      else None()

      threads = threads :+
        ReportBlock(
          tag = createTag(s"slang-code-${thread.identifier}-$tnick"),
          content = Some(
            st"""[${tnick}](${project.projectRootDir.relativize(behavior.get)})
                |${gumboxOpt}""")
        )
    }
    val description = st"""The following items link to the Slang source code for the application logic of each thread.
                          |In the HAMR development workflow, skeletons for these files are automatically created,
                          |along with APIs for communicating over model-declared ports in the component type.
                          |GUMBO component contracts in the AADL model are automatically translated to Slang/Logika
                          |contracts and included in the generated skeletons. Then, the application developer uses a
                          |conventional development approach for coding the application logic in Slang
                          |(C workflows are also supported). Logika can be applied to verify that the user's
                          |application code conforms to the generated Logika contracts (which are derived
                          |automatically from model-level GUMBO contracts). The HAMR build framework will integrate
                          |the user-code application logic for each component (below) with auto-generated threading
                          |and communication infrastructure code, along with HAMR's implementation of AADL run-time
                          |(based on AADL's standardized Run-Time Services). Note that HAMR is smart enough to
                          |accommodate changes to model-level interface declarations (ports, etc.) as well as changes
                          |to GUMBO contracts -- user code will not be clobbered when the model is changed and HAMR
                          |code generation is rerun. Instead, HAMR uses specially designed delimiters in the
                          |application code files to, e.g., re-weave updated contracts into the application code.
                          |
                          |Executable Slang versions of the GUMBO contracts (referred to as "GUMBOX" contracts)
                          |are also automatically generated in the code generation process. These executable
                          |contracts are automatically integrated into the unit testing process: appropriate
                          |portions of the executable contracts are invoked in the pre-state and the post-state
                          |of a thread dispatch to dynamically check that the thread's behavior for that particular
                          |dispatch conforms to the model-level GUMBO contracts."""
    return ReportLevel(
      tag = createTag("behavior-code"),
      title = Some(st"Behavior Code"),
      description = Some(description),
      content = threads,
      subLevels = ISZ()
    )
  }


  def genReport(project: Project): ReportLevel = {

    val model = AadlModelUtil.getModel(project.air, F)
    val table = AadlModelUtil.getSymbolTable(model, packageName, project.configs(0))

    val arch: ReportLevel = genArchitectureSection(project, table)

    val behaviorCode: ReportLevel = genBehaviorCodeSection(project, table)

    val metrics: ReportLevel = genCodeMetrics(project)

    val unitTesting: ReportLevel = genTestingSection(project, table)

    //val logika: ReportLevel = genLogikaSection(project, table)

    //val howToRun: ReportLevel = genHowToRunSection(project, table)

    return ReportLevel(
      tag = createTag(project.title),
      title = Some(st"${project.title}"),
      description = Some(st"The data, links, and images in this file are auto-generated from HAMR's report generation facility. Additional text explanations have been added for readability."),
      content = ISZ(),
      subLevels = ISZ(arch, behaviorCode, metrics, unitTesting)//, howToRun)
    )
  }
}