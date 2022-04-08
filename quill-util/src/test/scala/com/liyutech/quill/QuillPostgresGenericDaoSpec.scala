package com.liyutech.quill

import com.liyutech.quill.model.OrcaUser
import io.getquill.Query
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}

import java.time.LocalDateTime
import io.getquill.PostgresJdbcContext

// TODO: This is integration test requiring connection to a remote db server. Test the logic using an embedded db instead.
class QuillPostgresGenericDaoSpec extends QuillBaseSpec with Checkers with ScalaCheckPropertyChecks {
  import QuillGenericDao.*
  import quillDao._
  import io.getquill.*
  //  override def beforeAll(): Unit = {
  //    val flyway = Flyway.configure.dataSource(quillDao.dataSource).schemas("orca").load
  //    flyway.clean()
  //    flyway.repair()
  //    println(s"Flyway: ${flyway.migrate()}")
  //    println("beforeAll 2")
  //  }

  private inline val usCitizenship = "US"
  inline def filter: OrcaUser => Boolean = _.citizenship == usCitizenship

  "QuillGenericDao find[T]" should "find records satisfying the given filter" in {
    val users = quillDao.find[OrcaUser](filter)
    assert(users.map(_.citizenship).forall(_ == usCitizenship))
  }
  "QuillGenericDao findBy[T]" should "find records whose ids match the given id" in {
    val users = quillDao.findBy[OrcaUser, String](usCitizenship, _.citizenship)
    assert(users.map(_.citizenship).forall(_ == usCitizenship))
  }
  "QuillGenericDao findAll[T]" should "find all records from a table" in {
    val allUsers = quillDao.findAll[OrcaUser]
    assert(allUsers.nonEmpty)
  }

  "QuillGenericDao findGroupMax[T, G, M]" should "groups records by the given groupBy function and find the maximal value for each group"  in {
    val maxRecords: Seq[OrcaUser] = quillDao.findGroupMax[OrcaUser, String, LocalDateTime](_.id, _.updatedAt)
    val groupByData: Map[String, LocalDateTime] = quillDao.findAll[OrcaUser].groupBy(_.id).map { case (groupId, models) => 
        val max: LocalDateTime = models.map(_.updatedAt).max
        (groupId, max)
      }

    val result: Boolean = maxRecords.forall { expectedMaxRecord =>
      val id = expectedMaxRecord.id
      groupByData.get(id).fold(false) { record => expectedMaxRecord == record }
    }

    println(s"groupByData ==>${groupByData.size}<==")
    assert(result)

    // val groupMax: Seq[OrcaUser] = quillDao.findGroupMax[OrcaUser, String, LocalDateTime](_.username, _.updatedAt) { (user, id, updatedAt) =>
    //   user.updatedAt == updatedAt && user.username == id
    // }

    // groupMax.foreach(println)

    // assert(groupMax.size > 0)


  }  
  // "QuillGenericDao findMaxFields[T]" should "groups records by a given id field and returns a tuple of the id and the max value for each group" in {
  //   // val entities: Seq[((String, String), String)] = quillDao.findMaxFields[OrcaUser, (String, String), String](user => (user.id, user.username), _.firstName)


  // //   val uQ = query[OrcaUser]
  // //   val rawQ = quote {
  // //    () => infix"""select id, id from orca.orca_user""".as[Query[(String, String)]]
  // //   }
  // //  val entities = quillDao.run(rawQ())
  // //  entities.foreach(println)
  // //   assert(entities.nonEmpty)
  // }
      //    val entities: Seq[((String, String), LocalDateTime)] = quillDao.findMaxFields[OrcaUser, (String, String), LocalDateTime](user => (user.id, user.username), _.updatedAt)


  // "QuillPostgresGenericDao findGroupMax()" should "find the maximum for records grouped by a given function" in {

  "QuillPostgresGenericDao findGroup2Max()" should "find the maximum for records grouped by a given function" ignore {
    
    import quillDao._
    import QuillGenericDao.*

    //    dynamicQuery[OrcaUser].groupBy[(String, String)] { user => (user.id, user.username) }.map {
    //      case ((col0, col1), models) =>
    //        val m: LocalDateTime = models.map(_.updatedAt).max.orNull
    //        (col0, col1, m)
    //    }

    import io.getquill.*

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
