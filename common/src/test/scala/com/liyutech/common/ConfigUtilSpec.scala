package com.liyutech.common

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.flatspec.AsyncFlatSpec

import java.io.File
import scala.reflect.io.Path

class ConfigUtilSpec extends AsyncFlatSpec {
  "ConfigUtil" should "combine typesafe Config objects" in {

    val tmpFilePath: String = "/tmp/actual_combined.conf"

    Path(tmpFilePath).delete()

    val classPathRoot = CommonUtil.classLoaderPath(this.getClass)

    Path(tmpFilePath).createFile().appendAll(CommonUtil.readFileAsString(s"${classPathRoot}secure.conf"))

    val expectedConfigFileName = "expected_combined.conf"
    val expectedConfigFile = new File(s"$classPathRoot$expectedConfigFileName")
    val expectedCombinedConf = ConfigFactory.parseFileAnySyntax(expectedConfigFile)
    val entrySet0 = expectedCombinedConf.entrySet()

    val actualCombinedConf: Config = ConfigUtil.loadConfig(new File(tmpFilePath), expectedConfigFile)
    val entrySet1 = actualCombinedConf.entrySet()
    assert {
      entrySet0.size() == entrySet1.size() && entrySet0.containsAll(entrySet1) && entrySet1.containsAll(entrySet0)
    }
  }
}
