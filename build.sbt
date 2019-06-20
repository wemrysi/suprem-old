import scala.collection.Seq

homepage in ThisBuild := Some(url("https://github.com/slamdata/suprem"))

scmInfo in ThisBuild := Some(ScmInfo(
  url("https://github.com/slamdata/suprem"),
  "scm:git@github.com:slamdata/suprem.git"))

lazy val root = project
  .in(file("."))
  .settings(noPublishSettings)
  .aggregate(core, scodec, testkit)
  .enablePlugins(AutomateHeaderPlugin)

lazy val core = project
  .in(file("core"))
  .settings(
    performMavenCentralSync := false,
    publishAsOSSProject := true)
  .enablePlugins(AutomateHeaderPlugin)

lazy val scodec = project
  .in(file("scodec"))
  .dependsOn(
    core,
    testkit % "compile->test")
  .settings(
    performMavenCentralSync := false,
    publishAsOSSProject := true)
  .settings(
    libraryDependencies += "org.scodec" %% "scodec-core" % "1.11.4")
  .enablePlugins(AutomateHeaderPlugin)

lazy val testkit = project
  .in(file("testkit"))
  .dependsOn(core)
  .settings(
    performMavenCentralSync := false,
    publishAsOSSProject := true)
  .settings(
    libraryDependencies ++= Seq(
      "org.scodec" %% "scodec-bits" % "1.1.12",
      "org.specs2" %% "specs2-core" % "4.3.6",
      "org.specs2" %% "specs2-scalacheck" % "4.3.6"))
  .enablePlugins(AutomateHeaderPlugin)
