name := "music-playlist"

version := "0.1"

scalaVersion := "2.13.1"

val circeVersion = "0.12.3"

//resolvers += Resolver.sonatypeRepo("releases")
//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
