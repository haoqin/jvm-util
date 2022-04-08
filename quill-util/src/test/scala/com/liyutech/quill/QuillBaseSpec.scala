package com.liyutech.quill

import com.liyutech.common.ConfigUtil
import com.typesafe.config.Config
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}
import io.getquill.PostgresJdbcContext
import io.getquill.SnakeCase

trait QuillBaseSpec extends AsyncFlatSpec with Checkers with ScalaCheckPropertyChecks {
  protected val config: Config = ConfigUtil.loadConfig("test-postgres")
  protected val dbConfig: Config = config.getConfig("db")
  protected val quillDao: PostgresJdbcContext[SnakeCase] = PostgresJdbcContext(SnakeCase, dbConfig)
}