import java.text.SimpleDateFormat
import java.util.Date
import io.gatling
import ReleaseTransformations._

import scala.util.Try

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

val javaTestOptions = "-Dconfig.file=conf/" + Option("test.application").getOrElse("application") + ".conf"

val buildSha = Try(Process("git rev-parse --short HEAD").!!.stripLineEnd).getOrElse("?")

lazy val Versions = new {
  val scalatest = "3.0.1"
  val phantom = "2.11.1"
  val util = "0.36.0"
  val quill = "1.2.1"
  val config = "1.3.1"
  val scalatestPlusPlay = "3.0.0-RC1"
  val gatling = "2.2.2"
}

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, JavaAppPackaging, BuildInfoPlugin, NewRelic, GatlingPlugin)
	.configs(IntegrationTest)
	.settings(Defaults.itSettings :_*)
  .settings(cassandraVersion := "3.9")
  .settings(
    name := "cassandra-loadtest",
    organization := "test",
    scalaVersion := "2.11.8",
    buildInfoPackage := "test",
    buildInfoKeys := Seq[BuildInfoKey](
      BuildInfoKey.action("buildDate")(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())),
      BuildInfoKey.action("buildSha")(buildSha),
      BuildInfoKey.action("name")(name.value),
      BuildInfoKey.action("version")(version.value),
      BuildInfoKey.action("scalaVersion")(scalaVersion.value),
      BuildInfoKey.action("sbtVersion")(sbtVersion.value),
      BuildInfoKey.action("dependencies")(Seq("cassandra"))
    ),
    fork in run := true,
    javaOptions in run ++= Seq(
      "-XX:MetaspaceSize=512m",
      "-XX:MaxMetaspaceSize=1g"
    ),
    libraryDependencies ++= Seq(
      "io.gatling.highcharts" % "gatling-charts-highcharts" % Versions.gatling % Test,
      "io.gatling" % "gatling-test-framework" % Versions.gatling % Test,
      "com.outworkers" %% "phantom-dsl" % Versions.phantom,
      "com.outworkers" %% "util-samplers" % Versions.util % Test,
      "io.getquill" %% "quill-cassandra" % Versions.quill,
      "com.typesafe" % "config" % Versions.config,
      "org.scalatestplus.play" %% "scalatestplus-play" % Versions.scalatestPlusPlay % "test,it",
      "org.mockito" % "mockito-core" % "2.7.22" % Test,
      "org.scalatest" %% "scalatest" % Versions.scalatest % Test
    ),

    TwirlKeys.templateFormats += ("yaml" -> "play.twirl.api.TxtFormat"),

    fork in IntegrationTest := true,

		sourceDirectory in IntegrationTest := baseDirectory.value / "it",
		scalaSource in IntegrationTest := baseDirectory.value / "it",
		testOptions in IntegrationTest += Tests.Argument(TestFrameworks.Specs2, "sequential", "true", "junitxml", "console"),
    javaOptions in Test += javaTestOptions,
    javaOptions in IntegrationTest += javaTestOptions,
    coverageExcludedPackages :="""controllers.javascript;controllers\..*Reverse.*;router.Routes.*;views.*;configuration""",
    coverageEnabled in(Test, compile) := true,
    coverageEnabled in(IntegrationTest, compile) := false,
    coverageEnabled in(Compile, compile) := false,
    coverageMinimum := 90,
    coverageFailOnMinimum := false
  )

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)
