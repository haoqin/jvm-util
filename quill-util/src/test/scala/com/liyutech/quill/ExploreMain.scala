package com.liyutech.quill

import io.getquill.*
import com.liyutech.quill.model.Person
import io.getquill.PostgresDialect

object ExploreMain {
  def main(args: Array[String]): Unit = {

    val values: Map[String, String] = Map("name" -> "Joe", "age" -> "22")

    // filterByKeys uses lift so you need a context to use it
    val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)

    import ctx.*

    inline def q = quote {
      query[Person].filterByKeys(values)
    }
    val aa = run(q)

    println(aa)
  }
}
