package com.liyutech.quill

import com.liyutech.quill.model.OrcaUser
import org.flywaydb.core.Flyway
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}

class QuillPostgresGenericDaoSpec extends AsyncFlatSpec with Checkers with ScalaCheckPropertyChecks with BeforeAndAfterAll {
  val quillDao = QuillGenericDao.defaultPostgresGenericDao("test-postgres")

  override def beforeAll(): Unit = {
    val flyway = Flyway.configure.dataSource(quillDao.dataSource).schemas("orca").load
    flyway.clean()
    flyway.repair()
    println(s"Flyway: ${flyway.migrate()}")
    println("beforeAll 2")
  }

  "QuillPostgresGenericDao find()" should "insert " in {
    // println(s"1111: ${quillDao.findAll[Pod].size}")
    println("===")
    quillDao.deleteAll[OrcaUser]
    println(quillDao.findAll[OrcaUser].size)
    assert(true)
  }

}
