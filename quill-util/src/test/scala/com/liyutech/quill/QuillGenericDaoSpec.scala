package com.liyutech.quill

import com.liyutech.quill.JdbcContextDecorators.JdbcContextDecorator
import com.liyutech.quill.model.{Transaction, User}

class QuillGenericDaoSpec extends QuillBaseSpec {

  "QuillGenericDao.insert() and find()" should "insert and then verify expected number of rows in a table" in forAll(userModels(modelsPerIteration)) { models =>
    val initCount = quillDao.findAll[User].size
    println(s"initCount: $initCount")
    val insertedRows: Seq[Long] = models.map(quillDao.insert(_))
    println(s"""insertedRows: ${insertedRows.mkString(",")}""")
    val endCount = quillDao.findAll[User].size
    println(s"endCount: $endCount")

    val result = quillDao.findSimpleUser()
    println(s"result 123: $result")

    assert(endCount - initCount == modelsPerIteration && insertedRows.sum == modelsPerIteration)
  }

  "QuillGenericDao.insertAutoIncremented() and find()" should "return auto-incremental keys after insertion to a table" in forAll(autoIncrementModels(modelsPerIteration)) { models =>
    val initCount = quillDao.findAll[Transaction].size

    println(s"initCount: $initCount")
    val primaryKeys: Seq[Int] = models.map { model =>
      quillDao.insertAutoIncremented[Transaction, Int](model, m => m.transactionId)
    }

    println("Primary keys:")
    println(primaryKeys.mkString(","))
    val endCount = quillDao.findAll[Transaction].size
    println(s"endCount: $endCount")
    assert(endCount - initCount == modelsPerIteration && primaryKeys.size == modelsPerIteration)
  }

  "QuillGenericDao.insertAll()" should "insert all rows within a transactional context" in forAll(userModels(modelsPerIteration)) { models =>
    val initCount = quillDao.findAll[User].size
    val primaryKeys: Seq[Long] = quillDao.insertAll[User](models)
    println(s"initCount: $initCount")
    println("Primary keys:")
    println(primaryKeys.mkString(","))
    val endCount = quillDao.findAll[User].size
    println(s"endCount: $endCount")
    assert(endCount - initCount == modelsPerIteration && primaryKeys.sum == modelsPerIteration)
  }
}