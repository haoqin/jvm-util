package com.liyutech.quill

import com.typesafe.config.Config
import io.getquill.context.jdbc.JdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.{H2Dialect, H2JdbcContext, NamingStrategy, SnakeCase}

import scala.language.experimental.macros

// Inspired by the examples from https://github.com/getquill/quill-example/tree/4723a5e482efb75b04371ad1d6410219b0893364
trait QuillGenericDao[D <: SqlIdiom, N <: NamingStrategy] extends JdbcContext[D, N] with QuillGenericMacro

class QuillH2GenericDao[N <: NamingStrategy](namingStrategy: N, dbConfig: Config) extends H2JdbcContext(namingStrategy, dbConfig) with QuillGenericDao[H2Dialect, N]

object QuillGenericDao {
  def snakeCaseH2GenericDao(dbConfig: Config): QuillH2GenericDao[SnakeCase] = new QuillH2GenericDao(SnakeCase, dbConfig)
}

