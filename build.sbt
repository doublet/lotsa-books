name := "book-tracker"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.1-1",
  "commons-validator" % "commons-validator" % "1.4.0"
)     

play.Project.playJavaSettings
