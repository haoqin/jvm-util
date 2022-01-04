package com.liyutech.quill

import com.liyutech.quill.model.OrcaUser
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}

class QuillPostgresGenericDaoSpec extends AsyncFlatSpec with Checkers with ScalaCheckPropertyChecks {
  val quillDao = QuillGenericDao.defaultPostgresGenericDao("test-postgres")

  //  override def beforeAll(): Unit = {
  //    val flyway = Flyway.configure.dataSource(quillDao.dataSource).schemas("orca").load
  //    flyway.clean()
  //    flyway.repair()
  //    println(s"Flyway: ${flyway.migrate()}")
  //    println("beforeAll 2")
  //  }

  "QuillPostgresGenericDao find()" should "insert " in {
    // println(s"1111: ${quillDao.findAll[Pod].size}")
    println("===")
    //    quillDao.deleteAll[OrcaUser]
    val allUsers = quillDao.findAll[OrcaUser]
    allUsers.foreach(println)
    assert(true)
  }

}
