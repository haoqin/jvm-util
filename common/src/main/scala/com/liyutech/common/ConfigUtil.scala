package com.liyutech.common

import com.typesafe.config.{Config, ConfigFactory}

object ConfigUtil {
  private[liyutech] val defaultEnv = "local"
  private[liyutech] val applicationConfigPrefix = "application-"
  private[liyutech] val applicationConfigSuffix = ".conf"
  private[liyutech] val dbConfigKey = "db"

  def loadConfig(env: String = defaultEnv): Config = {
    val configFilePath = s"${applicationConfigPrefix}${env}$applicationConfigSuffix"
    println(s"Config file: $configFilePath")
    ConfigFactory.load(configFilePath)
  }
}
