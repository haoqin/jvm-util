package com.liyutech.quill

import com.liyutech.quill.model.OrcaUser
import io.getquill.Query
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}

import java.time.LocalDateTime

class QuillPostgresGenericDaoSpec extends AsyncFlatSpec with Checkers with ScalaCheckPropertyChecks {
  val quillDao = QuillGenericDao.defaultPostgresGenericDao("test-postgres")

  //  override def beforeAll(): Unit = {
  //    val flyway = Flyway.configure.dataSource(quillDao.dataSource).schemas("orca").load
  //    flyway.clean()
  //    flyway.repair()
  //    println(s"Flyway: ${flyway.migrate()}")
  //    println("beforeAll 2")
  //  }

  "QuillPostgresGenericDao findGroupMax()" should "find the maximum for records grouped by a given function" in {
    // println(s"1111: ${quillDao.findAll[Pod].size}")
    println("===")
    //    quillDao.deleteAll[OrcaUser]
    val allUsers = quillDao.findAll[OrcaUser]
    allUsers.foreach(println)

    val groupMax: Seq[OrcaUser] = quillDao.findGroupMax[OrcaUser, String, LocalDateTime](_.username, _.updatedAt) { (user, id, updatedAt) =>
      user.updatedAt == updatedAt && user.username == id
    }

    groupMax.foreach(println)

    assert(groupMax.size > 0)
  }

  "QuillPostgresGenericDao findGroup2Max()" should "find the maximum for records grouped by a given function" in {
    import quillDao._

    //    dynamicQuery[OrcaUser].groupBy[(String, String)] { user => (user.id, user.username) }.map {
    //      case ((col0, col1), models) =>
    //        val m: LocalDateTime = models.map(_.updatedAt).max.orNull
    //        (col0, col1, m)
    //    }


    def sqlGen(tableName: String, maxBy: String, groupBy: String, moreGroupBy: String): Quoted[Query[OrcaUser]] = {
      quote {
        infix"""SELECT m.* FROM (SELECT *,ROW_NUMBER() OVER(PARTITION BY #$groupBy, #$moreGroupBy ORDER BY #$maxBy DESC) rowNumber FROM #$tableName) m WHERE m.rowNumber = 1""".as[Query[OrcaUser]]
      }
    }

    import com.liyutech.common.CollectionUtil.ResultSetDecorator
    val allUsers = quillDao.findAll[OrcaUser]

    val groupMaxes = allUsers.findGroupMax(u => (u.uid, u.username), u => u.updatedAt)

    groupMaxes.foreach(println)

//    val users = quillDao.run(sqlGen("orca_user", "updated_at", "uid", "username"))
//    val users = quillDao.findGroup2Max[OrcaUser]("orca_user", "updated_at", "uid", "username")

    //    val users = quillDao.findGroup2Max[OrcaUser]("orca_user", "updated_at", "uid", "username")

    //    val entities: Seq[((String, String), LocalDateTime)] = quillDao.findMaxFields[OrcaUser, (String, String), LocalDateTime](user => (user.id, user.username), _.updatedAt)
    //
    //    val entities: Seq[(String, String, LocalDateTime)] = quillDao.run(query[OrcaUser].groupBy[(String, String)] { user => (user.id, user.username) }.map {
    //      case ((col0, col1), models) =>
    //        val m: LocalDateTime = models.map(_.updatedAt).max.orNull
    //        (col0, col1, m)
    //    })
    //    val entities: Seq[(String, String, LocalDateTime)] = quillDao.run(q0)
    //    val users: Seq[OrcaUser] = entities.flatMap { case (gv0, gv1, m) =>
    //      val q = quillDao.quote {
    //        quillDao.query[OrcaUser].filter { user => user.id == lift(gv0) && user.username == lift(gv1) && user.updatedAt == lift(m) }
    //      }
    //      quillDao.run(q)
    //    }

    //    val group2Max: Seq[OrcaUser] = quillDao.findGroup2Max[OrcaUser, String, String, LocalDateTime](user => (user.uid, user.username), user => user.updatedAt) { (user, uid, userName, updatedAt) =>
    //      user.uid == uid && user.username == userName && user.updatedAt == updatedAt
    //    }
    //    group2Max.foreach(println)

    assert(groupMaxes.size > 0)
  }

}
