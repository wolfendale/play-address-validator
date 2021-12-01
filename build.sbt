
name := "play-address-validator"

version := "0.1"

scalaVersion := "2.12.15"

scalacOptions += "-Ypartial-unification"

enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.3.0",
  guice,
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalamock" %% "scalamock" % "5.1.0" % Test
)
