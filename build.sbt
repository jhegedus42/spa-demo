import sbt.Keys._
import sbt.Project.projectToRef

// a special crossProject for configuring a JS/JVM/shared structure
lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := Settings.versions.scala,
    libraryDependencies ++= Settings.sharedDependencies.value
  )
  // set up settings specific to the JS project
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")

lazy val sharedJS = shared.js.settings(name := "sharedJS")

// use eliding to drop some debug code in the production build
lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

// instantiate the JS project for SBT with some additional settings
lazy val client: Project = (project in file("client"))
  .settings(
    name := "client",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.scalajsDependencies.value,
    // by default we do development build, no eliding
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,
    jsDependencies ++= Settings.jsDependencies.value,
    // RuntimeDOM is needed for tests
    jsDependencies += RuntimeDOM % "test",
    // yes, we want to package JS dependencies
    skip in packageJSDependencies := false,
    // use Scala.js provided launcher code to start the client app
    persistLauncher := true,
    persistLauncher in Test := false,
    // use uTest framework for tests
    testFrameworks += new TestFramework("utest.runner.Framework"),
    mainClass in Compile := Some("spatutorial.client.SPAMain")
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJS)

// Client projects (just one in this case)
lazy val clients = Seq(client)


val app = crossProject.in(file("spray_simple")).settings(
  unmanagedSourceDirectories in Compile +=
    baseDirectory.value  / "shared" / "main" / "scala",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % "0.6.2",
    "com.lihaoyi" %%% "upickle" % "0.4.4"
  ),
  scalaVersion := "2.11.8"
).jsSettings(
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
    "com.typesafe.akka" %% "akka-actor" % "2.4.12",
    "org.webjars" % "bootstrap" % "3.2.0",
    "io.spray" %% "spray-can" % "1.3.3",
    "io.spray" %% "spray-routing" % "1.3.3",
    "com.typesafe.akka" %% "akka-actor" % "2.3.6"
  )
)

// instantiate the JVM project for SBT with some additional settings
lazy val spray_simple= (project in file("spray_simple"))
  .settings(
      name := "spray_simple",
      version := Settings.version,
      scalaVersion := Settings.versions.scala,
      scalacOptions ++= Settings.scalacOptions,
//      libraryDependencies ++= Settings.jvmDependencies.value
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "scalatags" % "0.6.2",
        "com.lihaoyi" %%% "upickle" % "0.4.4",
        "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
        "com.typesafe.akka" %% "akka-actor" % "2.4.12",
        "org.webjars" % "bootstrap" % "3.2.0",
        "io.spray" %% "spray-can" % "1.3.3",
        "io.spray" %% "spray-routing" % "1.3.3"
        )
  )
//  .dependsOn(sharedJVM)


// instantiate the JVM project for SBT with some additional settings
lazy val server = (project in file("server"))
  .settings(
    name := "server",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.jvmDependencies.value,
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
    // connect to the client project
    scalaJSProjects := clients,
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // compress CSS
    LessKeys.compress in Assets := true
  )
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin) // use the standard directory layout instead of Play's custom
  .aggregate(clients.map(projectToRef): _*)
  .dependsOn(sharedJVM)


lazy val root = (project in file(".")).aggregate(client, server, spray_simple)

// loads the Play server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
mainClass in Global := Some("com.danielasfregola.quiz.management.Main")
fork in run := true
cancelable in Global := true


lazy val appJS = app.js
lazy val appJVM = app.jvm.settings(
  (resources in Compile) += (fastOptJS in (appJS, Compile)).value.data
)
