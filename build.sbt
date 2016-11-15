import sbt.Keys._
import spray.revolver.RevolverPlugin.Revolver

scalaVersion := "2.11.8"

val hackaton = crossProject.settings(
  scalaVersion := "2.11.8",
  version := "0.100",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.4.3",
    "com.lihaoyi" %%% "autowire" % "0.2.6",
    "com.lihaoyi" %%% "scalatags" % "0.6.1"
  )
).jsSettings(
  name := "Client",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1"
  )
).jvmSettings(
  Revolver.settings:_*
).jvmSettings(
  name := "Server",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11"
  )
)

val hackatonJS = hackaton.js
val hackatonJVM = hackaton.jvm.settings(
  (resources in Compile) += {
    (fastOptJS in (hackatonJS, Compile)).value
    (artifactPath in (hackatonJS, Compile, fastOptJS)).value
  }
)
