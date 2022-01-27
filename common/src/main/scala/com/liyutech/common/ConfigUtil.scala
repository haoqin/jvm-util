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

  // Notice that the content of internalConfigFile will be appended to externalConfigFile, the latter which will be
  // deleted after the configurations are loaded into the memory. In other words, the externalConfigFile is supposed to
  // be ephemeral.
  // The client is responsible for passing the correct files.
  def loadConfig(externalConfigFile: File, internalConfigFile: File): Config = {
    val secretConfigFilePath = externalConfigFile.getAbsolutePath
    println(s"secretConfigFilePath  $secretConfigFilePath exists: ${externalConfigFile.exists()}")
    println(s"internalConfigFile    ${internalConfigFile.getAbsolutePath} exists: ${internalConfigFile.exists()}")

    val resourceContent: String = CommonUtil.readAsString(internalConfigFile)
    println(s"resourceContent resourceContent size ${resourceContent.size} ")

    Path(secretConfigFilePath).createFile().appendAll(
      "\n",
      resourceContent
    )
    val conf: Config = ConfigFactory.parseFileAnySyntax(new File(secretConfigFilePath))
    Path(secretConfigFilePath).delete()
    conf
  }
}
