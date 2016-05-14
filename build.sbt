

name := "CrateSpark"

version := "1.0"

scalaVersion := "2.11.8"

organization := "com.cisco"

sparkVersion := "1.6.1"

val testSparkVersion = settingKey[String]("The version of Spark to test against.")

testSparkVersion := sys.props.get("spark.testVersion").getOrElse(sparkVersion.value)

val testHadoopVersion = settingKey[String]("The version of Hadoop to test against.")

testHadoopVersion := sys.props.getOrElse("hadoop.testVersion", "2.2.0")

sparkComponents := Seq("core", "sql")

resolvers += Resolver.bintrayRepo("crate", "crate")

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.startsWith("META-INF") => MergeStrategy.discard
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList("org", "apache", xs @ _*) => MergeStrategy.first
  case PathList("org", "jboss", xs @ _*) => MergeStrategy.first
  case "about.html" => MergeStrategy.rename
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-client" % testHadoopVersion.value % "compile",
  "org.apache.spark" %% "spark-core" % testSparkVersion.value % "compile" force() exclude("org.apache.hadoop", "hadoop-client"),
  "org.apache.spark" %% "spark-sql" % testSparkVersion.value % "compile" force() exclude("org.apache.hadoop", "hadoop-client"),
  "org.scala-lang" % "scala-library" % scalaVersion.value % "compile",
  "org.slf4j" % "slf4j-api" % "1.7.5" % "provided",
  "io.crate" % "crate-jdbc" % "1.11.0"
)
