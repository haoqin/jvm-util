package com.liyutech.common

import com.typesafe.config.{Config, ConfigFactory}

object ConfigUtil {
  val defaultEnv = "local"
  val applicationConfigPrefix = "application-"
  val applicationConfigSuffix = ".conf"
  val dbConfigKey = "db"

  def loadConfig(env: String = defaultEnv): Config = {
    val configFilePath = s"${applicationConfigPrefix}${env}$applicationConfigSuffix"
    ConfigFactory.load(configFilePath)
  }
}
