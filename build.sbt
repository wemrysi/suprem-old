import scala.collection.Seq

homepage in ThisBuild := Some(url("https://github.com/slamdata/suprem"))

scmInfo in ThisBuild := Some(ScmInfo(
  url("https://github.com/slamdata/suprem"),
  "scm:git@github.com:slamdata/suprem.git"))

lazy val root = project
  .in(file("."))
  .settings(noPublishSettings)
  .aggregate(core)
  .enablePlugins(AutomateHeaderPlugin)

lazy val core = project
  .in(file("core"))
  .settings(
    performMavenCentralSync := false,
    publishAsOSSProject := true)
  .enablePlugins(AutomateHeaderPlugin)
