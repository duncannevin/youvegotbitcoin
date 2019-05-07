enablePlugins(JavaAppPackaging, AshScriptPlugin)

name := "youvegotbitcoin"

version := "0.1"

dockerBaseImage := "openjdk:8-jre-alpine"
packageName in Docker := "youvegotbitcoin"

val akkaVersion = "2.5.13"
val akkaHttpVersion = "10.1.3"
val circeVersion = "0.9.3"
val bitcoinjVersion = "0.15"
val akkaGuiceVersion = "3.2.0"
val mongoDriverVersion = "2.4.0"
val twirlVersion = "1.4.0"

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.21.0",

  "org.scalatest" %% "scalatest" % "3.0.5" % Test,

  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.11.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.0" % Runtime,

  "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
  "com.sandinh" %% "akka-guice" % akkaGuiceVersion,

  "org.bitcoinj" % "bitcoinj-core" % bitcoinjVersion % "compile",

  "org.mongodb.scala" %% "mongo-scala-driver" % mongoDriverVersion,

  "com.github.nscala-time" %% "nscala-time" % "2.22.0",

  "com.lihaoyi" %% "scalatags" % "0.6.7",

  "com.google.zxing" % "core" % "3.3.0",
  "com.google.zxing" % "javase" % "3.3.0",

  "com.github.giftedprimate" %% "scala_opennode" % "0.3"
)

mainClass in Global := Some("com.duncannevin.youvegotbitcoin.Main")
