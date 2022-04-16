package com.liyutech.quill

import com.liyutech.common.ConfigUtil
import com.typesafe.config.Config
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}
import io.getquill.PostgresJdbcContext
import io.getquill.SnakeCase
import io.getquill.H2JdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.NamingStrategy
import io.getquill.context.jdbc.JdbcContext
import com.liyutech.quill.model.OrcaUser
import java.time.LocalDateTime

trait QuillBaseSpec extends AsyncFlatSpec with Checkers with ScalaCheckPropertyChecks

object QuillBaseSpec:
  lazy val h2Config: Config = ConfigUtil.loadConfig("H2")
  lazy val h2DbConfig: Config = h2Config.getConfig("db")
  lazy val h2Dao: H2JdbcContext[SnakeCase] = new H2JdbcContext(SnakeCase, h2DbConfig)
  lazy val postgresConfig: Config = ConfigUtil.loadConfig("Postgres")
  lazy val postgresDbConfig: Config = postgresConfig.getConfig("db")
  lazy val postgresDao: PostgresJdbcContext[SnakeCase] = PostgresJdbcContext(SnakeCase, postgresDbConfig)