organization := """com.hunorkovacs"""

name := """koauth-proxy-finagle"""

version := "1.0-SNAPSHOT"

scalaVersion  := "2.10.4"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  "Spray Repository" at "http://repo.spray.io"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.hunorkovacs" %% "koauth" % "1.0-SNAPSHOT",
  "com.twitter" %% "finagle-http" % "6.2.0",
  "com.amazonaws" % "aws-java-sdk" % "1.8.7",
  "org.specs2" %% "specs2" % "2.3.12" % "test",
  "redis.clients" % "jedis" % "2.5.1",
  "io.spray" % "spray-caching" % "1.3.1",
  "net.codingwell" %% "scala-guice" % "4.0.0-beta4",
  "com.lambdaworks" % "scrypt" % "1.4.0"
)
