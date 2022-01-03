package com.liyutech.quill

trait QuillPostgresBaseSpec extends QuillBaseSpec {
  protected val quillDao = new QuillPostgresGenericDao(dbConfig)
}