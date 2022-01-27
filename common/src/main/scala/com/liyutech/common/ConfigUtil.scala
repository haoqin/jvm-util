package com.liyutech.common

import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import scala.reflect.io.Path

object ConfigUtil {
  val defaultEnv = "local"
  val applicationConfigPrefix = "application-"
  val applicationConfigSuffix = ".conf"
  val dbConfigKey = "db"

  def loadConfig(env: String = defaultEnv): Config = {
    val configFilePath = s"$applicationConfigPrefix$env$applicationConfigSuffix"
    ConfigFactory.load(configFilePath)
  }

  def loadConfig(secretConfigFilePath: String, applicationConfPath: String): Config = {
    Path(secretConfigFilePath).createFile().appendAll(
      "\n",
      CommonUtil.readFileAsOptionString(applicationConfPath, ".").fold(".")(identity)
    )
    val conf: Config = ConfigFactory.parseFileAnySyntax(new File(secretConfigFilePath))
    Path(secretConfigFilePath).delete()
    conf
  }
}
