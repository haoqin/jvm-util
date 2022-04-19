package com.liyutech.quill

import io.getquill.NamingStrategy
import io.getquill.context.jdbc.JdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.*
case class SimpleUser(username: String, firstName: String, lastName: String)

// Explore a cost effective way to extend Quill JdbcContext.
object JdbcContextDecorators {
  extension[D <: SqlIdiom, N <: NamingStrategy](ctx: JdbcContext[D, N]) {
    inline def findSimpleUser(): List[SimpleUser] = {
      import ctx._
      run(querySchema[SimpleUser]("user"))
    }
  }
}