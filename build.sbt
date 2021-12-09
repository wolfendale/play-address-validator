
name := "play-address-validator"

version := "0.1"

scalaVersion := "2.12.15"

scalacOptions += "-Ypartial-unification"

enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalamock" %% "scalamock" % "5.1.0" % Test
)
