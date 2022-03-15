package com.liyutech.quill

import com.liyutech.common.data.ScalaCheckGenerator
import com.liyutech.common.data.ScalaCheckGenerator.{randomAlphaNumericStr, randomEmail, randomEmoji, randomTwoUpperCamelNouns, randomUSAddress, randomUSFirstName, randomUSLastName, randomUSPhoneNumber, randomUUID}
import com.liyutech.common.data.codegen.ArbitraryProduct
import com.liyutech.common.{CommonUtil, USCommons, USZipCodeInfo}
import com.liyutech.quill.model.{OrcaUser, Pod}
import org.scalacheck.Gen

import java.util.{Base64, Date}
import scala.util.Random

trait TestDataGen extends ScalaCheckGenerator {
  protected lazy val userModelGen: Gen[OrcaUser] = ArbitraryProduct.arbitrary[OrcaUser].map(toReadableUser(_))

  protected def userModels(n: Int): Gen[Seq[OrcaUser]] = Gen.listOfN(n, userModelGen)

  protected val DefaultCitizenship = "US"
  protected val allUSZipcodeInfo: Array[USZipCodeInfo] = USCommons.allUSZipcodeInfos
  protected val allUSZipcodeInfoCount = allUSZipcodeInfo.length
  protected val earliestBirthdayFromNow = 365 * 100
  protected val earliestUpdatedAtTimeFromNow = 365

  protected def autoIncrementModelGen: Gen[Pod] = ArbitraryProduct.arbitrary[Pod].map(toReadablePod(_))

  protected def autoIncrementModels(n: Int): Gen[Seq[Pod]] = Gen.listOfN(n, autoIncrementModelGen)

  protected val modelsPerIteration = 20

  protected def toReadableUser(user: OrcaUser): OrcaUser = {
    val randomFirstName = randomUSFirstName().toLowerCase
    val randomLastName = randomUSLastName().toLowerCase
    val randomUserName = s"$randomFirstName.$randomLastName"
    val usZipcodeInfoIndex = Random.nextInt(allUSZipcodeInfoCount)
    val nextRandomUSZipCode = allUSZipcodeInfo(usZipcodeInfoIndex)
    user.copy(
      id = randomUUID(),
      uid = randomUUID(),
      personId = Option(randomUUID()),
      profileImage = new String(Base64.getEncoder.encode(randomAlphaNumericStr(20).getBytes)),
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
  }

  protected def toReadablePod(model: Pod): Pod = {
    val podDescription: String = randomTwoUpperCamelNouns()
    val podUserName: String = podDescription.toLowerCase.replaceAll(" ", ".")
    model.copy(
      id = randomUUID(),
      podUsername = podUserName,
      podName = Option(podUserName),
      podDescription = Option(podDescription),
      emoji = model.emoji.map(_ => randomEmoji),
      updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow))
    )
  }
}
