val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "quill-repro",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "com.h2database" % "h2" % "2.1.212",
    libraryDependencies += "io.getquill" %% "quill-jdbc" % "3.16.5-Beta30"
  )
