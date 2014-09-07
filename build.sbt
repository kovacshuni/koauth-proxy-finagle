organization := """com.hunorkovacs"""

name := """koauth-sample-proxy-finagle"""

version := "1.0-SNAPSHOT"

scalaVersion  := "2.10.4"

resolvers ++= Seq(
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.hunorkovacs" %% "koauth" % "1.0-SNAPSHOT",
  "com.twitter" %% "finagle-http" % "6.2.0"
)
