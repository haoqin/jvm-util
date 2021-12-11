package com.liyutech.quill

import com.liyutech.quill.model.{Transaction, User}

class QuillGenericDaoSpec extends QuillBaseSpec {
  "QuillGenericDao.insert() and find()" should "insert and then verify expected number of rows in a table" in forAll(userModels(modelsPerIteration)) { models =>
    val initCount = quillDao.findAll[User].size
    println(s"initCount: $initCount")
    val insertedRows: Seq[Long] = models.map(quillDao.insert(_))
    println(s"""insertedRows: ${insertedRows.mkString(",")}""")
    val endCount = quillDao.findAll[User].size
    println(s"endCount: $endCount")
    assert(endCount - initCount == modelsPerIteration && insertedRows.sum == modelsPerIteration )
  }

  "QuillGenericDao.insertWithReturn() and find()" should "return auto-incremental keys after insertion to a table" in forAll(autoIncrementModels(modelsPerIteration)) { models =>
    val initCount = quillDao.findAll[Transaction].size

    println(s"initCount: $initCount")
    val primaryKeys: Seq[Int] = models.map { model =>
      quillDao.insertWithReturn[Transaction, Int](model, m => m.transactionId)
    }

    println("Primary keys:")
    println(primaryKeys.mkString(","))
    val endCount = quillDao.findAll[Transaction].size
    println(s"endCount: $endCount")
    assert(endCount - initCount == modelsPerIteration && primaryKeys.size == modelsPerIteration)
  }

  "QuillGenericDao.insertAll()" should "insert all rows within a transactional context" in forAll(autoIncrementModels(modelsPerIteration)) { models =>
    val initCount = quillDao.findAll[Transaction].size
    val primaryKeys: Seq[Int] = quillDao.insertAll[Transaction, Int](models, _.transactionId)
    println(s"initCount: $initCount")
    println("Primary keys:")
    println(primaryKeys.mkString(","))
    val endCount = quillDao.findAll[Transaction].size
    println(s"endCount: $endCount")
    assert(endCount - initCount == modelsPerIteration && primaryKeys.size == modelsPerIteration)
  }
}