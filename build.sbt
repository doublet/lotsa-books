name := "book-tracker"

version := "1.0-SNAPSHOT"


libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.1-1",
  "commons-validator" % "commons-validator" % "1.4.0",
  "org.json" % "json" % "20140107"
)     

play.Project.playJavaSettings

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
	val _ = initialize.value
	if (sys.props("java.specification.version") != "1.8")
	sys.error("Java 8 is required for this project.")
}