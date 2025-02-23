// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.5")

// The JaCoCo Plugin, see https://github.com/sbt/sbt-jacoco
addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "3.5.0")

// Defines scaffolding (found under .g8 folder)
// http://www.foundweekends.org/giter8/scaffolding.html
// sbt "g8Scaffold form"
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.16.2")
