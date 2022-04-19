package com.liyutech.quill

import com.liyutech.common.meta.TypeUtil
import com.liyutech.quill.QuillBaseSpec
import com.liyutech.quill.QuillGenericJdbcContext
import com.liyutech.quill.model.OrcaUser
import java.time.LocalDateTime
import com.liyutech.common.meta.DataGen
import com.liyutech.common.USZipCodeInfo
import org.scalatest.*
import scala.deriving.Mirror
import com.liyutech.common.meta.RandomGen
import com.liyutech.quill.model.Pod
import com.liyutech.quill.model.PodAutokey
import scala.util.Random

class QuillGenericJdbcContextSpec extends QuillBaseSpec with TestDataGen with BeforeAndAfterEach {
  val quillDao = QuillBaseSpec.h2Dao
  import quillDao.*
  import io.getquill.*
  import QuillGenericJdbcContext.*
  import DataGen.{given, *}
  import TypeUtil.*
  private inline val usCitizenship = "US"
  private inline def citizenshipFilter: OrcaUser => Boolean = _.citizenship == usCitizenship
  private inline def stateFilter: OrcaUser => Boolean = user => user.state.isDefined // && user.state.fold(false)(_.length == 2)
  private inline def userFilter: OrcaUser => Boolean = u => citizenshipFilter(u) && stateFilter(u)

  private inline def userQuote: OrcaUser => Boolean = quote((u: OrcaUser) => citizenshipFilter(u) && stateFilter(u))


  override def beforeEach() = {
    quillDao.deleteAll[OrcaUser]()
    insertUsers(modelsPerIteration)
  }

  private def generateRandomUsers(n: Int): Seq[OrcaUser] = 
    (0 until modelsPerIteration).map(_ => generateRandom[OrcaUser]).map(toReadableUser)

  private def insertUsers(n: Int): Seq[OrcaUser] = {
    val models: Seq[OrcaUser] = generateRandomUsers(n)
    quillDao.insertAll[OrcaUser](models)
    models
  }


  private def generateRandomPods(n: Int): Seq[PodAutokey] = 
    (0 until modelsPerIteration).map(_ => generateRandom[PodAutokey]).map(toReadablePodAutoKey)

  private def insertPods(n: Int): Seq[PodAutokey] = {
    val models: Seq[PodAutokey] = generateRandomPods(n)
    quillDao.insertAll[PodAutokey](models)
    models
  }

  "QuillGenericDao findBy[T]" should "find records whose ids match the given id" in {
    val users = quillDao.findBy[OrcaUser, String](usCitizenship, _.phoneNumber)
    assert(users.forall(citizenshipFilter))
  }

  "QuillGenericDao findBy[T]" should "find records whose ids are in the list of predefined ids" in {
    val n = 30
    val phoneNumbers = Seq("123-456-7890", "123-456-7891")

    val moreUsers = generateRandomUsers(n).zipWithIndex.map { case (user, i) =>
      val phoneNumber = phoneNumbers(i % phoneNumbers.size)
      user.copy(phoneNumber = phoneNumber)
    }
    quillDao.insertAll(moreUsers)
    val users = quillDao.findBy[OrcaUser, String](phoneNumbers, _.phoneNumber)
    users.map(_.phoneNumber).foreach(println)
    assert(users.forall(u => phoneNumbers.contains(u.phoneNumber)))
  }

  "QuillGenericDao findMax[T]" should "find the maximal/latest record whose id match the given id" in {

    val randomUser: OrcaUser = toReadableUser(generateRandom[OrcaUser])
    val updatedAt: LocalDateTime = randomUser.updatedAt
    // duplicate the same user n times, with the updatedAt fields in the descending order

    val updatedUserRecords: Seq[OrcaUser] = (0 until modelsPerIteration).map(i => randomUser.copy(updatedAt = updatedAt.minusDays(i)))
    quillDao.insertAll(updatedUserRecords)
    val latestUserRecord: Option[OrcaUser] = quillDao.findMax[OrcaUser, String, LocalDateTime](randomUser.uid, _.uid, _.updatedAt)
    val nonExistingRecord: Option[OrcaUser] = quillDao.findMax[OrcaUser, String, LocalDateTime](randomUser.uid + randomUser.uid , _.uid, _.updatedAt)
    assert(nonExistingRecord.isEmpty && latestUserRecord.isDefined && latestUserRecord.get.updatedAt.isEqual(updatedAt) )
  }
  

  "QuillGenericDao findAll[T]" should "find all records from a table" in {
    val allUsers = quillDao.findAll[OrcaUser]
    assert(allUsers.size == modelsPerIteration)
  }

  "QuillGenericDao findGroupMax[T, G, M]" should "groups records by the given groupBy function and find the maximal value for each group" in {
    val maxRecords: Seq[OrcaUser] = quillDao.findGroupMax[OrcaUser, String, LocalDateTime](_.id, _.updatedAt)
    val groupByData: Map[String, LocalDateTime] = quillDao.findAll[OrcaUser].groupBy(_.id).map { case (groupId, models) => 
        val max: LocalDateTime = models.map(_.updatedAt).max
        (groupId, max)
      }

    val result: Boolean = maxRecords.forall { expectedMaxRecord =>
      val id = expectedMaxRecord.id
      groupByData.get(id).fold(false)(_.isEqual(expectedMaxRecord.updatedAt))
    }
    assert(result)
  } 

  "QuillGenericDao.insertAll() and find()" should "insert and then verify expected number of rows in a table" in {
    val initCount = quillDao.findAll[OrcaUser].size
    println(s"initCount: $initCount")
    val insertedRows: Seq[OrcaUser] = insertUsers(modelsPerIteration)
    println(s"""insertedRows: ${insertedRows.size}""")
    val updatedRows: Seq[OrcaUser] = quillDao.findAll[OrcaUser]
    val endCount = updatedRows.size
    println(s"endCount: $endCount")
    val modelsMatched: Boolean = insertedRows.forall(updatedRows.contains)
    assert(endCount - initCount == modelsPerIteration && insertedRows.size == modelsPerIteration && modelsMatched)
  }

  "QuillGenericDao.autogeneratedKeyAfterInsert()" should "return auto-incremental keys after insertion to a table" in { 
    quillDao.deleteAll[PodAutokey]()
     val models: Seq[PodAutokey] = generateRandomPods(modelsPerIteration)

    val primaryKeys: Seq[Int] = quillDao.autoKeysAfterInsert[PodAutokey, Int](models, _.id)
    println("Primary keys:")
    println(primaryKeys.mkString(","))
    val diffs: Seq[Int] = primaryKeys.init.zip(primaryKeys.tail).map { case ((a, b)) => a - b }
    println("Primary key sliding differences:")
    println(diffs.mkString(","))

    val isIncremental: Boolean = diffs.forall(_ == -1)
    println(s"Primary key sliding difference isIncremental: $isIncremental")

    val endCount = quillDao.findAll[PodAutokey].size
    println(s"endCount: $endCount")
    assert(endCount == modelsPerIteration && models.size == modelsPerIteration && isIncremental)
  }
}