organization := """com.hunorkovacs"""

name := """koauth-sample-spray"""

version := "1.0-SNAPSHOT"

scalaVersion  := "2.11.1"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
  "io.spray" % "spray-can_2.11" % "1.3.1",
  "io.spray" % "spray-routing_2.11" % "1.3.1",
  "org.json4s" % "json4s-native_2.11" % "3.2.10",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.4",
  "com.hunorkovacs" % "koauth_2.11" % "1.0-SNAPSHOT"
)
