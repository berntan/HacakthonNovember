enablePlugins(ScalaJSPlugin)

name := "Hackaton"

scalaVersion := "2.11.8"

version := "0.1"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "scalatags" % "0.6.1"
)
