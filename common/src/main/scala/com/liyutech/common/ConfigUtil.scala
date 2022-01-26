package com.liyutech.common

import com.typesafe.config.{Config, ConfigFactory, ConfigList, ConfigObject, ConfigValue, ConfigValueType}

import java.util

object ConfigUtil {
  val defaultEnv = "local"
  val applicationConfigPrefix = "application-"
  val applicationConfigSuffix = ".conf"
  val dbConfigKey = "db"

  def loadConfig(env: String = defaultEnv): Config = {
    val configFilePath = s"$applicationConfigPrefix$env$applicationConfigSuffix"
    ConfigFactory.load(configFilePath)
  }

  implicit class ConfigDecorator(conf: Config) {
    // If an element is present in both Configs, the value in the second set is ignored.
    def combine(anotherConf: Config): Config = {
      val firstEntries = conf.entrySet()
      val secondEntries = anotherConf.entrySet()
      val combined: java.util.Map[String, ConfigValue] = new util.HashMap[String, ConfigValue]()
      firstEntries.forEach { entry =>
        val key: String = entry.getKey
        val firstValue: ConfigValue = entry.getValue
        if (anotherConf.hasPath(key)) {
          val secondValue: ConfigValue = anotherConf.getValue(key)
          (firstValue.valueType(), secondValue.valueType()) match {
            case (ConfigValueType.OBJECT, ConfigValueType.OBJECT) =>
              val combinedObj: Config = firstValue.asInstanceOf[ConfigObject].toConfig
                .combine(secondValue.asInstanceOf[ConfigObject].toConfig)
              combined.put(key, combinedObj.asInstanceOf[ConfigObject])
            // TODO: Handle list concatenation later. ConfigList.addAll(ConfigList) does not work.
            //              Exception in thread "main" java.lang.UnsupportedOperationException: ConfigList is immutable, you can't call List.'addAll'
            //          at com.typesafe.config.impl.SimpleConfigList.weAreImmutable(SimpleConfigList.java:387)
            //            case (ConfigValueType.LIST, ConfigValueType.LIST) =>
            //              val aList = firstValue.asInstanceOf[ConfigList]
            //              aList.addAll(secondValue.asInstanceOf[ConfigList])
            //              combined.put(key, aList)
            case (_, _) => combined.put(key, firstValue)
          }
        }
        else {
          combined.put(key, firstValue)
        }
      }

      secondEntries.forEach { entry =>
        val key: String = entry.getKey
        combined.putIfAbsent(key, entry.getValue)
      }

      ConfigFactory.parseMap(combined)
    }
  }
}
