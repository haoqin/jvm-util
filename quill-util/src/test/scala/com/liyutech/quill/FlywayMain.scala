package com.liyutech.quill

import com.liyutech.common.ConfigUtil
import io.getquill.{H2JdbcContext, SnakeCase}
import org.flywaydb.core.Flyway
import io.getquill.PostgresJdbcContext

object FlywayMain {
  def main(args: Array[String]): Unit = {
    val config = ConfigUtil.loadConfig("test-postgres")
    val dbConfig = config.getConfig("db")
    val quillDao = new PostgresJdbcContext[SnakeCase](SnakeCase, dbConfig)
    val flyway = Flyway.configure.dataSource(quillDao.dataSource).load
    println(s"Flyway: ${flyway.migrate()}")
  }
}
