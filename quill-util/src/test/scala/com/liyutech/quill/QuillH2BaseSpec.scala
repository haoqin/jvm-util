package com.liyutech.quill

import io.getquill.SnakeCase

trait QuillH2BaseSpec extends QuillBaseSpec {
  protected val quillDao = new QuillH2GenericDao(SnakeCase, dbConfig)
}