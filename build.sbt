
name := "Eric scala"

version := "0.0.1"

organization := "com.eric"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Spray Nightlies" at "http://nightlies.spray.io/",
  "bigtoast-github" at "http://bigtoast.github.com/repo/"
)

resolvers += Resolver.url("Typesafe Repository (ivy)", url("http://repo.typesafe.com/typesafe/release/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= {
  val akkaVersion = "2.3.4"
  val sprayVersion = "1.3.1"
  var testVersion = "3.0.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.16",

    "org.scalatest" %% "scalatest" % testVersion,

    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-json" % sprayVersion,
    "mysql" % "mysql-connector-java" % "5.1.37",
    "net.debasishg" %% "redisclient" % "3.3"
  )
}


assemblyMergeStrategy in assembly := {
  case PathList("org", "joda", "time", "base", "BaseDateTime.class") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
