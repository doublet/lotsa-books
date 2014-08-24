name := "book-tracker"

version := "1.0-SNAPSHOT"


libraryDependencies ++= Seq(
  javaWs,
  javaJdbc,
  javaEbean,
  cache,
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.webjars" % "jquery" % "2.1.1",
  "commons-validator" % "commons-validator" % "1.4.0",
  "org.json" % "json" % "20140107"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
	val _ = initialize.value
	if (sys.props("java.specification.version") != "1.8")
	sys.error("Java 8 is required for this project.")
}