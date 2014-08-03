organization := """com.hunorkovacs"""

name := """koauth-proxy-finagle"""

version := "1.0-SNAPSHOT"

scalaVersion  := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.hunorkovacs" %% "koauth" % "1.0-SNAPSHOT",
  "com.twitter" %% "finagle-http" % "6.2.0",
  "com.amazonaws" % "aws-java-sdk" % "1.8.7",
  "org.specs2" %% "specs2" % "2.3.12" % "test"
)
