lazy val QuillVersion = "3.10.0"

ThisBuild / organization    := "com.liyutech"
ThisBuild / scalaVersion    := "2.13.4"
ThisBuild / version := "0.0.1"

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "io.getquill"                %% "quill-codegen-jdbc"        % QuillVersion,
    "io.getquill"                %% "quill-jdbc"                % QuillVersion,
    "io.getquill"                %% "quill-jdbc-zio"            % QuillVersion,
    "io.getquill"                %% "quill-jasync-postgres"     % QuillVersion,
    "io.getquill"                %% "quill-async-postgres"      % QuillVersion,
    "org.scalatest"              %% "scalatest"                 % "3.1.4"                 % Test,
    "org.scalatest"              %% "scalatest-flatspec"        % "3.2.5"                 % Test,
    "org.scalacheck"             %% "scalacheck"                % "1.15.2"                % Test,
    "org.scalatestplus"          %% "scalacheck-1-15"           % "3.2.2.0"               % Test
  ),
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _ => MergeStrategy.first},
  Global / onChangedBuildSource := ReloadOnSourceChanges
)
lazy val root = (project in file("."))
  .settings(
    name := "quill-util",
    commonSettings
  ).aggregate(gen, api)
  .dependsOn(gen, api)

lazy val gen = (project in file("gen")).settings( name := "quill-util-gen", commonSettings)
lazy val api = (project in file("api")).settings( name := "quill-util-api", commonSettings)
  .aggregate(gen).dependsOn(gen)
