
name := "autoapp"

organization := "com.scout24"

version := "1.0.0"

scalaVersion := "2.12.4"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.5"
  val scalaTestVersion = "3.0.1"
  val scalaMockV = "3.5.0"
  val slickVersion = "3.2.0"
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.play" %% "play-json" % "2.6.0-M6",

    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "org.slf4j" % "slf4j-nop" % "1.7.25",
    "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
    "org.flywaydb" % "flyway-core" % "3.2.1",

    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % scalaMockV % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.h2database" % "h2" % "1.4.194" % Test
  )
}

lazy val root = project.in(file(".")).configs(IntegrationTest)
Defaults.itSettings

Revolver.settings

enablePlugins(JavaAppPackaging)

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := (scalastyle in Compile).toTask("").value

(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value



// code coverage configuration
coverageEnabled := false

coverageHighlighting := true

coverageMinimum := 100

coverageFailOnMinimum := true

parallelExecution in Test := false
