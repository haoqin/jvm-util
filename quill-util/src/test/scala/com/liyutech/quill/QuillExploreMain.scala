package com.liyutech.quill

import com.liyutech.quill.model.User
import com.typesafe.config.Config
import io.getquill.{H2JdbcContext, SnakeCase}

object QuillExploreMain {
  private val config: Config = ConfigUtil.loadConfig("test")
  private val dbConfig: Config = config.getConfig("db")
  private val h2Dao = new H2JdbcContext(SnakeCase, dbConfig)
  //  private val simpleUserQuery = h2Dao.quote {
  //    h2Dao.query[User]
  //    //.map(u => (u.username, u.firstName, u.lastName))
  //  }

  //    println(s"simpleUserQuery: $simpleUserQuery")
  //    h2Dao.run(simpleUserQuery)
  def main(args: Array[String]): Unit = {
    import h2Dao._
    val q = quote {
      query[User]
    }
    println(run(q))
  }

}
