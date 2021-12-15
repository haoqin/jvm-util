package com.liyutech.quill

import io.getquill.{H2JdbcContext, SnakeCase}
import org.flywaydb.core.Flyway

object FlywayMain {
  def main(args: Array[String]): Unit = {
    val config = ConfigUtil.loadConfig("test")
    val dbConfig = config.getConfig("db")
    val quillDao = new H2JdbcContext(SnakeCase, dbConfig) with QuillGenericMacro
    val flyway = Flyway.configure.dataSource(quillDao.dataSource).load
    println(s"Flyway: ${flyway.migrate()}")
  }
}
