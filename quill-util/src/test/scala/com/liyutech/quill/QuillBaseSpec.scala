package com.liyutech.quill

import com.liyutech.common.ConfigUtil
import com.typesafe.config.Config
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}

trait QuillBaseSpec extends AsyncFlatSpec with Checkers with ScalaCheckPropertyChecks with TestDataGen {


  protected val config: Config = ConfigUtil.loadConfig("test-postgres")
  protected val dbConfig: Config = config.getConfig("db")

}