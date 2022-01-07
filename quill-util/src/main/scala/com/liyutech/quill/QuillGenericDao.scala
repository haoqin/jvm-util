package com.liyutech.quill

import com.liyutech.common.ConfigUtil
import com.typesafe.config.Config
import io.getquill.{H2JdbcContext, NamingStrategy, PostgresJdbcContext, SnakeCase}

import scala.language.experimental.macros

// Inspired by the examples from https://github.com/getquill/quill-example/tree/4723a5e482efb75b04371ad1d6410219b0893364
class QuillH2GenericDao[N <: NamingStrategy](namingStrategy: N, dbConfig: Config) extends H2JdbcContext(namingStrategy, dbConfig) with QuillGenericMacro

//class QuillPostgresGenericDao(dbConfig: Config) extends PostgresJdbcContext(CompositeNamingStrategy2[LowerCase, SnakeCase](LowerCase, SnakeCase), dbConfig) with QuillGenericMacro
class QuillPostgresGenericDao(dbConfig: Config) extends PostgresJdbcContext(SnakeCase, dbConfig) with QuillGenericMacro

object QuillGenericDao {
  def snakeCaseH2GenericDao(dbConfig: Config): QuillH2GenericDao[SnakeCase] = new QuillH2GenericDao(SnakeCase, dbConfig)

  def defaultPostgresGenericDao(dbConfig: Config): QuillPostgresGenericDao = new QuillPostgresGenericDao(dbConfig)

  def defaultPostgresGenericDao(env: String, dbPath: String = "db"): QuillPostgresGenericDao = {
    val config: Config = ConfigUtil.loadConfig(env)
    val dbConfig: Config = config.getConfig(dbPath)
    defaultPostgresGenericDao(dbConfig)
  }

  //  implicit class QuillPostgresGenericDaoDecorator(dao: QuillPostgresGenericDao) {
  //    def findGroup2Max[T: dao.SchemaMeta](tableName: String, maxBy: String, groupBy: String, moreGroupBy: String): Seq[T] = {
  //      import dao._
  //      dao.run(quote {
  //        infix"""SELECT m.* FROM (SELECT *,ROW_NUMBER() OVER(PARTITION BY #$groupBy, #$moreGroupBy ORDER BY #$maxBy DESC) rowNumber FROM #$tableName) m WHERE m.rowNumber = 1""".as[Query[T]]
  //      })
  //    }
  //  }
}
