package com.liyutech.quill

import com.liyutech.common.data.ScalaCheckGenerator
import com.liyutech.common.data.ScalaCheckGenerator.{randomAlphaNumericStr, randomEmail, randomEmoji, randomTwoUpperCamelNouns, randomUSAddress, randomUSFirstName, randomUSLastName, randomUSPhoneNumber, randomUUID}
import com.liyutech.common.data.codegen.ArbitraryProduct
import com.liyutech.common.{CommonUtil, USCommons, USZipCodeInfo}
import com.liyutech.quill.model._
import org.scalacheck.Prop

import java.util.{Base64, Date}
import scala.util.Random

object GenTestData extends ScalaCheckGenerator with TestDataGen {

  implicit val strTransform: String => String = _.take(3)
  private val quillDao = QuillGenericDao.defaultPostgresGenericDao("test-postgres")


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

      quillDao.deleteAll[Expense]
      quillDao.deleteAll[OrcaTransaction]
      quillDao.deleteAll[UserBalance]
    }


    Prop.forAll(ArbitraryProduct.arbitrary[OrcaUser]) { user =>
      val newModel: OrcaUser = toReadableUser(user)
      println(newModel)
      val insertCount: Long = quillDao.insert[OrcaUser](newModel)
      println(s"User inserted: $insertCount")
      insertCount == 1
    }.check()

    Prop.forAll(ArbitraryProduct.arbitrary[Pod]) { model =>
      val podDescription: String = randomTwoUpperCamelNouns()
      val podUserName: String = podDescription.toLowerCase.replaceAll(" ", ".")
      val newModel: Pod = toReadablePod(model)
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

    val shuffledUsers = Random.shuffle(users)
    val shuffledPods = Random.shuffle(pods)

    var i = 0
    Prop.forAll(ArbitraryProduct.arbitrary[Expense]) { model =>
      val randomExpenseId = randomUUID()
      val randomSenderName = shuffledUsers(i).id
      val randomPodUserId = shuffledPods(i).podUsername
      val randomAmount = Random.nextInt(1000).toDouble
      val expenseUnit = if (randomAmount > 1.0) "dollars" else "dollar"
      val randomMessage = s"Expense $randomAmount $expenseUnit from ${shuffledUsers(i).username}"
      val newModel: Expense = model.copy(
        senderUserId = randomSenderName,
        id = randomExpenseId,
        podUserId = randomPodUserId,
        amount = randomAmount,
        message = model.message.map(_ => randomMessage),
        updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow)))
      val insertCount: Long = quillDao.insert[Expense](newModel)
      println(s"Expense inserted: $insertCount")
      i += 1
      insertCount == 1
    }.check()

    val expenses: Seq[Expense] = quillDao.findAll[Expense]
    val shuffledExpenses: Seq[Expense] = Random.shuffle(expenses)

    i = 0
    // TODO: Can transaction data be inferred from expenses?
    Prop.forAll(ArbitraryProduct.arbitrary[OrcaTransaction]) { model =>
      val randomExpense = shuffledExpenses(i)
      val randomSenderName = randomExpense.senderUserId
      val remainingUserIndexes = (0 until shuffledUsers.size).filterNot(_ == i)
      val receiverUserIndex = Random.shuffle(remainingUserIndexes).head
      val randomReceiverName = shuffledUsers(receiverUserIndex).id
      val randomExpenseId = randomExpense.id
      val randomAmount = randomExpense.amount
      val transactionUnit = if (randomAmount > 1.0) "dollars" else "dollar"

      val newModel: OrcaTransaction = model.copy(
        id = randomUUID(),
        podUserId = shuffledPods(i).podUsername,
        expenseId = randomExpenseId,
        senderUserId = randomSenderName,
        receiverUserId = randomReceiverName,
        amount = randomAmount,
        bookingId = model.bookingId.map(_ => randomUUID()),
        message = model.message.map(_ => s"Transferred $randomAmount $transactionUnit from ${shuffledUsers(i).username} to ${shuffledUsers(receiverUserIndex).username}"),
        updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow))
      )
      val insertCount: Long = quillDao.insert[OrcaTransaction](newModel)
      println(s"Transaction inserted: $insertCount")
      i += 1
      insertCount == 1
    }.check()

    val orcaTransactions: Seq[OrcaTransaction] = quillDao.findAll[OrcaTransaction]
    i = 0

    // Make a deposit to the UserBalance for all receivers of orca transactions.
    // Shall we also deduce the same amount from the corresponding senders?
    orcaTransactions.foreach { orcaTransaction =>
      val newModel = UserBalance(
        id = randomUUID(),
        userId = orcaTransaction.receiverUserId,
        transactionId = orcaTransaction.id,
        balance = orcaTransaction.amount,
        updatedAt = orcaTransaction.updatedAt
      )
      val insertCount: Long = quillDao.insert[UserBalance](newModel)
      println(s"User balance inserted: $insertCount")
    }

    //    Prop.forAll(ArbitraryProduct.arbitrary[UserBalance]) { model =>
    //      val randomUserName = shuffledUsers(i).id
    //      val randomTransactionId = orcaTransactions(i).id
    //      val newModel: UserBalance = model.copy(
    //        id = randomUUID(),
    //        userId = randomUserName,
    //        transactionId = randomTransactionId,
    //        balance = Math.abs(model.balance),
    //        updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow))
    //      )
    //
    //      val insertCount: Long = quillDao.insert[UserBalance](newModel)
    //      println(s"User balance inserted: $insertCount")
    //      i += 1
    //      insertCount == 1
    //    }.check()
  }

}
