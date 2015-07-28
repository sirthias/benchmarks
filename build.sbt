import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

lazy val benchmarks = (project in file("."))
  .enablePlugins(JmhPlugin)
  .settings(scalariformSettings: _*)
  .settings(
    scalaVersion := "2.11.7",
    organization := "com.decodified",
    homepage := Some(new URL("http://github.com/sirthias/benchmarks")),
    description := "A collection of potentially interesting JMH benchmarks for the JVM",
    startYear := Some(2015),
    licenses := Seq("Apache-2.0" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    javacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-source", "1.7",
      "-target", "1.7",
      "-Xlint:unchecked",
      "-Xlint:deprecation"),
    scalacOptions ++= List(
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-language:_",
      "-target:jvm-1.7",
      "-Xlog-reflective-calls"),
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(PreserveDanglingCloseParenthesis, true),
    mainClass in (Compile, run) := None, // disable sbt-jmh default running, we'll always use `runMain` to target a specific benchmark
    libraryDependencies ++= Seq(
      "io.spray" %%  "spray-json" % "1.3.2",
      "org.json4s" %% "json4s-native" % "3.2.11",
      "org.json4s" %% "json4s-jackson" % "3.2.11",
      "io.argonaut" %% "argonaut" % "6.1",
      "org.spire-math" %% "jawn-spray" % "0.8.2"))