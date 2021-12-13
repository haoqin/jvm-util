import sbt.url

ThisBuild / organization := "com.liyutech"
ThisBuild / organizationName := "liyutech"
ThisBuild / scalaVersion := "2.13.4"

ThisBuild / organizationHomepage := Some(url("https://github.com/haoqin/jvm-util"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/haoqin/jvm-util"),
    "scm:git@github.com:haoqin/jvm-util"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "hqin",
    name  = "liyutech",
    email = "hqin@liyutech.com",
    url   = url("https://github.com/haoqin/jvm-util")
  )
)

ThisBuild / description := "Common jvm util libraries not readily available under Apache Commons"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/haoqin/jvm-util"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true