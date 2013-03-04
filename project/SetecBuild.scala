import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object SetecBuild extends Build {

  lazy val project = Project(
    id = "setec",
    base = file("."),
    settings = Defaults.defaultSettings ++ assemblySettings ++ Seq(
      organization := "org.codeswarm",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0",
      libraryDependencies ++= Seq(
        "org.rogach" %% "scallop" % "0.8.1"
      ),
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "1.9.1"
      ) map (_ % "test"),
      jarName in assembly := "setec.jar"
    )
  )

}
