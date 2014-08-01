organization := """com.hunorkovacs"""

name := """koauth-sample-spray"""

version := "1.0-SNAPSHOT"

scalaVersion  := "2.10.4"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
  "io.spray" %% "spray-can" % "1.3.1",
  "io.spray" %% "spray-routing" % "1.3.1",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.hunorkovacs" %% "koauth" % "1.0-SNAPSHOT",
  "com.twitter" %% "finagle-http" % "6.2.0"
)
