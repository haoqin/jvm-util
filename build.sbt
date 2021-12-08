lazy val QuillVersion = "3.10.0"
lazy val ScalaVersion = "3.2.9"
ThisBuild / organization    := "com.liyutech"
ThisBuild / scalaVersion    := "2.13.4"
ThisBuild / version := "0.0.2"

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe"               %  "config"                    % "1.4.1",
    "org.scalatest"              %% "scalatest"                 % ScalaVersion                 % Test,
    "org.scalatest"              %% "scalatest-flatspec"        % ScalaVersion                 % Test,
    "org.scalacheck"             %% "scalacheck"                % "1.15.4"                     % Test,
    "org.scalatestplus"          %% "scalacheck-1-15"           % "3.2.9.0"                    % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.5"
  ),
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", _ @ _*) => MergeStrategy.discard
    case _ => MergeStrategy.first},
  Global / onChangedBuildSource := ReloadOnSourceChanges
)

lazy val quillSettings = Seq(
  libraryDependencies ++= Seq(
    "io.getquill"                %% "quill-codegen-jdbc"        % QuillVersion,
    "io.getquill"                %% "quill-jdbc"                % QuillVersion,
    "io.getquill"                %% "quill-jdbc-zio"            % QuillVersion,
  )
)
lazy val root = (project in file("."))
  .settings(
    name := "jvm-util",
  ).aggregate(common, gen, genTest)
  .dependsOn(common, gen, genTest)

lazy val common = (project in file("common")).settings( name := "common", commonSettings)
lazy val gen = (project in file("quill-gen"))
  .settings( name := "quill-gen", commonSettings, quillSettings)
  .dependsOn(common)
lazy val genTest = (project in file("quill-gen-test"))
  .settings(
    name := "quill-gen-test",
    libraryDependencies ++= Seq(
      "com.h2database"             %  "h2"                        % "1.4.199",
      "org.flywaydb"               %  "flyway-core"               % "8.1.0"
    ),
    commonSettings,
    quillSettings)
  .aggregate(gen).dependsOn(gen)
