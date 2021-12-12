package com.liyutech.quill

import io.getquill.NamingStrategy
import io.getquill.context.jdbc.JdbcContext
import io.getquill.context.sql.idiom.SqlIdiom

case class SimpleUser(username: String, firstName: String, lastName: String)

// Explore a cost effective way to extend Quill JdbcContext.
object JdbcContextDecorators {
  implicit class JdbcContextDecorator[D <: SqlIdiom, N <: NamingStrategy](ctx: JdbcContext[D, N]) {
    def findSimpleUser(): List[SimpleUser] = {
      import ctx._
      ctx.run(querySchema[SimpleUser]("user"))
    }
  }
}