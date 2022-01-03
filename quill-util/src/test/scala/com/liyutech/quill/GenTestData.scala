package com.liyutech.quill

import com.liyutech.common.data.ScalaCheckGenerator
import com.liyutech.common.data.ScalaCheckGenerator.{randomAlphaNumericStr, randomEmail, randomEmoji, randomTwoUpperCamelNouns, randomUSAddress, randomUSFirstName, randomUSLastName, randomUSPhoneNumber, randomUUID}
import com.liyutech.common.data.codegen.ArbitraryProduct
import com.liyutech.common.{CommonUtil, USCommons, USZipCodeInfo}
import com.liyutech.quill.model._
import org.scalacheck.Prop

import java.util.{Base64, Date}
import scala.util.Random

object GenTestData extends ScalaCheckGenerator {

  implicit val strTransform: String => String = _.take(3)
  private val quillDao = QuillGenericDao.defaultPostgresGenericDao("test-postgres")

  private val DefaultCitizenship = "US"
  private val allUSZipcodeInfo: Array[USZipCodeInfo] = USCommons.allUSZipcodeInfos
  private val allUSZipcodeInfoCount = allUSZipcodeInfo.length
  private val earliestBirthdayFromNow = 365 * 100
  private val earliestUpdatedAtTimeFromNow = 365

  def main(args: Array[String]): Unit = {
    val dropExistingRecordsFirst = args.headOption.fold(true)(_ == "deleteFirst")
    println(s"Deleting UserBalance table: $dropExistingRecordsFirst")

    if (dropExistingRecordsFirst) {
      // Delete tables with foreign keys first.
      println("Deleting user pod look up table")
      quillDao.deleteAll[UserPodLookup]

      println("Deleting UserBalance table")
      quillDao.deleteAll[UserBalance]

      println("Deleting transaction table")
      quillDao.deleteAll[OrcaTransaction]

      println("Deleting expense table")
      quillDao.deleteAll[Expense]

      // Delete tables without dependencies.
      println("Deleting user table")
      quillDao.deleteAll[OrcaUser]

      println("Deleting pod table")
      quillDao.deleteAll[Pod]
    }


    Prop.forAll(ArbitraryProduct.arbitrary[OrcaUser]) { user =>
      val randomFirstName = randomUSFirstName().toLowerCase
      val randomLastName = randomUSLastName().toLowerCase
      val randomUserName = s"$randomFirstName.$randomLastName"
      val usZipcodeInfoIndex = Random.nextInt(allUSZipcodeInfoCount)
      val nextRandomUSZipCode = allUSZipcodeInfo(usZipcodeInfoIndex)
      val newModel: OrcaUser = user.copy(
        id = randomUUID(),
        uid = randomUUID(),
        personId = Option(randomUUID()),
        profileImage = new String(Base64.getEncoder.encode(randomAlphaNumericStr(20000).getBytes)),
        firstName = randomFirstName,
        lastName = randomLastName,
        username = randomUserName,
        emailAddress = randomEmail(randomUserName),
        streetLine_1 = Option(randomUSAddress),
        streetLine_2 = None,
        dateOfBirth = CommonUtil.minusDays(new Date(), Random.nextLong(earliestBirthdayFromNow)),
        citizenship = DefaultCitizenship, postalCode = Option(nextRandomUSZipCode.zipCode.toString),
        state = Option(nextRandomUSZipCode.stateCode), phoneNumber = randomUSPhoneNumber(),
        city = Option(nextRandomUSZipCode.city),
        updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow)))
      println(newModel)
      val insertCount: Long = quillDao.insert[OrcaUser](newModel)
      println(s"User inserted: $insertCount")
      insertCount == 1
    }.check()

    Prop.forAll(ArbitraryProduct.arbitrary[Pod]) { model =>
      val podDescription: String = randomTwoUpperCamelNouns()
      val podUserName: String = podDescription.toLowerCase.replaceAll(" ", ".")
      val newModel: Pod = model.copy(
        id = randomUUID(),
        podUsername = podUserName,
        podName = Option(podUserName),
        podDescription = Option(podDescription),
        emoji = model.emoji.map(_ => randomEmoji),
        updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow))
      )
      val insertCount: Long = quillDao.insert[Pod](newModel)
      println(s"Pod inserted: $insertCount")
      insertCount == 1
    }.check()

    // Create tables with foreign key constraints
    val users: Seq[OrcaUser] = quillDao.findAll[OrcaUser]
    println(s"Users: ${users.size}")
    val pods: Seq[Pod] = quillDao.findAll[Pod]
    println(s"Pods: ${pods.size}")

    // TODO: Is the table join logic correct.
    val lookups: Seq[UserPodLookup] = users.zip(pods).map { case (user, pod) =>
      UserPodLookup(id = user.id, userId = user.id, podId = pod.id, isActive = Option(Random.nextBoolean()),
        permission = Option(Random.nextInt(10)),
        updatedAt = java.time.LocalDateTime.now().minusDays(Random.nextInt(10000)))
    }
    val inserted: Seq[Long] = quillDao.insertAll[UserPodLookup](lookups)
    println(s"User pod look up inserted rows: ${inserted.sum}")

    val userIds = users.map(_.id)
    val podIds = pods.map(_.id)

    Prop.forAll(ArbitraryProduct.arbitrary[Expense]) { model =>
      val randomSenderName = Random.shuffle(userIds).head
      val randomPodId = Random.shuffle(podIds).head
      val newModel: Expense = model.copy(
        senderUserId = randomSenderName,
        id = randomPodId,
        updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow)))
      val insertCount: Long = quillDao.insert[Expense](newModel)
      println(s"Expense inserted: $insertCount")
      insertCount == 1
    }.check()

    val expenses: Seq[Expense] = quillDao.findAll[Expense]
    val expenseIds = expenses.map(_.id)

    Prop.forAll(ArbitraryProduct.arbitrary[OrcaTransaction]) { model =>
      val shuffledUserIds = Random.shuffle(userIds)
      val randomSenderName = shuffledUserIds.head
      val randomReceiverName = shuffledUserIds.last
      val randomExpenseId = Random.shuffle(expenseIds).head
      val newModel: OrcaTransaction = model.copy(
        expenseId = randomExpenseId,
        senderUserId = randomSenderName,
        receiverUserId = randomReceiverName,
        updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow))
      )
      val insertCount: Long = quillDao.insert[OrcaTransaction](newModel)
      println(s"Transaction inserted: $insertCount")
      insertCount == 1
    }.check()

    val orcaTransactions: Seq[OrcaTransaction] = quillDao.findAll[OrcaTransaction]
    val orcaTransactionIds: Seq[String] = orcaTransactions.map(_.id)

    Prop.forAll(ArbitraryProduct.arbitrary[UserBalance]) { model =>
      val randomUserName = Random.shuffle(userIds).head
      val randomTransactionId = Random.shuffle(orcaTransactionIds).head
      val newModel: UserBalance = model.copy(
        userId = randomUserName,
        transactionId = randomTransactionId,
        balance = Math.abs(model.balance),
        updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow))
      )

      val insertCount: Long = quillDao.insert[UserBalance](newModel)
      println(s"User balance inserted: $insertCount")
      insertCount == 1
    }.check()
  }

}
