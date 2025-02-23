name := """TubeLytics"""
organization := "com.soen"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.15"

crossPaths := false

libraryDependencies += guice
libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "2.6.0",
  "com.google.code.gson" % "gson" % "2.10.1",
  "com.google.apis" % "google-api-services-youtube" % "v3-rev20241010-2.0.0",
  "org.daisy.dotify" % "dotify.api" % "5.0.7",
  "io.github.cdimascio" % "dotenv-java" % "3.0.0",
  "org.daisy.dotify" % "dotify.hyphenator.impl" % "5.0.1",
  "org.mockito" % "mockito-core" % "5.11.0" % "test",
  "org.jsoup" % "jsoup" % "1.18.1",
  "org.apache.pekko" %% "pekko-testkit" % "1.0.3" % Test,
  "org.junit.jupiter" % "junit-jupiter-api" % "5.10.3",
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.10.2",
  "org.junit.vintage" % "junit-vintage-engine" % "5.10.3"

)

PlayKeys.devSettings += "play.server.websocket.periodic-keep-alive-max-idle" -> "10 seconds"
PlayKeys.devSettings += "play.server.websocket.periodic-keep-alive-mode" -> "pong"
