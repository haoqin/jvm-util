package com.liyutech.quill

import io.getquill.SnakeCase
import io.getquill.H2JdbcContext

trait QuillH2BaseSpec extends QuillBaseSpec {
  val quillH2Dao = new H2JdbcContext(SnakeCase, dbConfig)
}