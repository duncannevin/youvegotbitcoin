addSbtPlugin(dependency = "com.typesafe.sbt" % "sbt-native-packager" % "1.3.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.4.0")
addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
