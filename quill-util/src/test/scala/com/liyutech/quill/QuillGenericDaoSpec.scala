package com.liyutech.quill

import com.liyutech.quill.model.OrcaUser

import java.time.LocalDateTime

class QuillGenericDaoSpec extends QuillPostgresBaseSpec {
  //
  //  "QuillGenericDao.insert() and find()" should "insert and then verify expected number of rows in a table" in forAll(userModels(modelsPerIteration)) { models =>
  //    val initCount = quillDao.findAll[User].size
  //    println(s"initCount: $initCount")
  //    val insertedRows: Seq[Long] = models.map(quillDao.insert(_))
  //    println(s"""insertedRows: ${insertedRows.mkString(",")}""")
  //    val endCount = quillDao.findAll[User].size
  //    println(s"endCount: $endCount")
  //
  //    val result = quillDao.findSimpleUser()
  //    println(s"result 123: $result")
  //
  //    assert(endCount - initCount == modelsPerIteration && insertedRows.sum == modelsPerIteration)
  //  }
  //
  //  "QuillGenericDao.insertAutoIncremented() and find()" should "return auto-incremental keys after insertion to a table" in forAll(autoIncrementModels(modelsPerIteration)) { models =>
  //    val initCount = quillDao.findAll[Transaction].size
  //
  //    println(s"initCount: $initCount")
  //    val primaryKeys: Seq[Int] = models.map { model =>
  //      quillDao.insertAutoIncremented[Transaction, Int](model, m => m.transactionId)
  //    }
  //
  //    println("Primary keys:")
  //    println(primaryKeys.mkString(","))
  //    val endCount = quillDao.findAll[Transaction].size
  //    println(s"endCount: $endCount")
  //    assert(endCount - initCount == modelsPerIteration && primaryKeys.size == modelsPerIteration)
  //  }
  //
  //  "QuillGenericDao.insertAll()" should "insert all rows within a transactional context" in forAll(userModels(modelsPerIteration)) { models =>
  //    val initCount = quillDao.findAll[User].size
  //    val primaryKeys: Seq[Long] = quillDao.insertAll[User](models)
  //    println(s"initCount: $initCount")
  //    println("Primary keys:")
  //    println(primaryKeys.mkString(","))
  //    val endCount = quillDao.findAll[User].size
  //    println(s"endCount: $endCount")
  //    assert(endCount - initCount == modelsPerIteration && primaryKeys.sum == modelsPerIteration)
  //  }
  //
  "QuillGenericDao.findMaxFields()" should "find all rows grouped by a given field that has the maximal value for a given field" in {
    import quillDao._

    val expectedMaxValues: Seq[(String, LocalDateTime)] = quillDao.findMaxFields[OrcaUser, String, LocalDateTime](_.username, _.updatedAt)
    println(expectedMaxValues)
    assert {
      expectedMaxValues.forall { case (username, expectedMaxValue) =>
        val actualMaxValue = quillDao.find[OrcaUser](_.username == lift(username)).map(_.updatedAt).max
        println(s"username: $username, expectedMaxValue: $expectedMaxValue, actualMaxValue: $actualMaxValue")
        expectedMaxValue == actualMaxValue
      }
    }
  }

  "QuillGenericDao.findGroupMax()" should "find all rows grouped by a given field that has the maximal value for a given field" ignore {
    // Make sure we have updates:
    val allUsers: Seq[OrcaUser] = quillDao.findAll[OrcaUser]
    val updatedUsers: Seq[OrcaUser] = allUsers.map { user => user.copy(updatedAt = user.updatedAt.plusDays(1L)) }
    quillDao.insertAll(updatedUsers)

    val expectedMaxValues: Map[String, LocalDateTime] = quillDao.findMaxFields[OrcaUser, String, LocalDateTime](_.username, _.updatedAt).toMap
    val maxRecords: Seq[OrcaUser] = quillDao.findGroupMax[OrcaUser, String, LocalDateTime](_.username, _.updatedAt) { (user, id, updatedAt) =>
      user.updatedAt == updatedAt && user.username == id
    }

    assert {
      maxRecords.forall { user =>
        println(s"user.username:${user.username}, user.updatedAt: ${user.updatedAt}, expected: ${expectedMaxValues.get(user.username)}")
        expectedMaxValues.get(user.username).fold(false)(_ == user.updatedAt)
      }
    }
  }

  "QuillGenericDao.findMax()" should "find all rows grouped by a given field that has the maximal value for a given field" in {
    val expectedMaxValues: Map[String, LocalDateTime] = quillDao.findMaxFields[OrcaUser, String, LocalDateTime](_.username, _.updatedAt).toMap
    assert {
      expectedMaxValues.forall { case (username, expectedMaxValue) =>
        val maxRecord: OrcaUser = quillDao.findMax[OrcaUser, String, LocalDateTime](username, _.username, _.updatedAt)
        maxRecord.updatedAt == expectedMaxValue
      }
    }
  }
}