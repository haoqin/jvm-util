lazy val QuillVersion = "3.17.0.Beta3.0-RC2"

lazy val CirceVersion = "0.14.1"
lazy val ScalaTestVersion = "3.2.11"

ThisBuild / organization := "com.liyutech"
ThisBuild / scalaVersion := "3.1.1"
ThisBuild / version := "1.0.0"
ThisBuild / versionScheme := Some("early-semver")

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.4.2",
    "io.circe" %% "circe-core" % CirceVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-parser" % CirceVersion,
    "org.typelevel" %% "shapeless3-deriving" % "3.0.4",
    "org.scalatest" %% "scalatest-flatspec" % ScalaTestVersion % Test,
    "org.scalacheck" %% "scalacheck" % "1.15.4",
    "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0"
  ),
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", _@_*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  },
  Global / onChangedBuildSource := ReloadOnSourceChanges
)

lazy val quillSettings = Seq(
  libraryDependencies ++= Seq(
    "io.getquill" %% "quill-jdbc" % QuillVersion
  )
)
//lazy val root = (project in file("."))
//  .settings(
//    name := "jvm-util",
//  ).aggregate(common, testUtil, gen, quillUtil)
//  .dependsOn(common, testUtil, gen, quillUtil)

lazy val root = (project in file("."))
  .settings(
    name := "jvm-util"
  ).aggregate(common, quillUtil)
  .dependsOn(common, quillUtil)

lazy val common = (project in file("common")).settings(name := "common", commonSettings)
lazy val testUtil = (project in file("test-util")).settings(name := "test-util", commonSettings)
  .aggregate(common)
  .dependsOn(common)

lazy val quillUtil = (project in file("quill-util"))
 .settings(name := "quill-util",
   libraryDependencies ++= Seq(
     "com.h2database" % "h2" % "1.4.199",
     "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
     "org.flywaydb" % "flyway-core" % "8.1.0"
   ),
   commonSettings,
   quillSettings)
   .aggregate(common)
 .dependsOn(common)