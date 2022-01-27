package com.liyutech.common

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.flatspec.AsyncFlatSpec

import java.io.File
import scala.reflect.io.Path

class ConfigUtilSpec extends AsyncFlatSpec {
  "ConfigUtil" should "combine typesafe Config objects" in {

    val tmpFilePath: String = "/tmp/actual_combined.conf"

    Path(tmpFilePath).delete()
    Path(tmpFilePath).createFile().appendAll(CommonUtil.readFileAsString("secure.conf"))
    val classPathRoot = CommonUtil.classLoaderPath(this.getClass)

    val expectedConfigFileName = "expected_combined.conf"
    val expectedCombinedConf = ConfigFactory.parseFileAnySyntax(new File(s"$classPathRoot$expectedConfigFileName"))
    val entrySet0 = expectedCombinedConf.entrySet()

    val actualCombinedConf: Config = ConfigUtil.loadConfig(tmpFilePath, expectedConfigFileName)
    val entrySet1 = actualCombinedConf.entrySet()
    assert {
      entrySet0.size() == entrySet1.size() && entrySet0.containsAll(entrySet1) && entrySet1.containsAll(entrySet0)
    }
  }
}
