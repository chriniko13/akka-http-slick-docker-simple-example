lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)

name := "akka-http-sample-with-docker"

version := "1.0"

scalaVersion := "2.11.8"

packageName in Docker := "akka-http-sample-with-docker"
dockerExposedPorts := Seq(5000)

libraryDependencies ++= {
  val akkaV = "2.4.7"
  val scalaTestV = "2.2.6"
  val slickV = "3.2.3"
  val slf4jV = "1.6.4"
  val mysqlConnV = "8.0.11"

  Seq(
    // AKKA Specific dependencies
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "org.scalatest" %% "scalatest" % scalaTestV % "test",

    // SLICK Specific dependencies
    "com.typesafe.slick" %% "slick" % slickV,
    "com.typesafe.slick" %% "slick-hikaricp" % slickV,
    "com.typesafe.slick" %% "slick-codegen" % slickV,
    "org.slf4j" % "slf4j-nop" % slf4jV,
    "mysql" % "mysql-connector-java" % mysqlConnV
  )
}
unmanagedResourceDirectories in Compile += {
  baseDirectory.value / "src/main/resources"
}