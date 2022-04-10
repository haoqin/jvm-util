package com.liyutech.quill
import org.scalacheck.Gen
import com.liyutech.quill.model.OrcaUser

// import com.liyutech.common.data.ScalaCheckGenerator
// import com.liyutech.common.data.ScalaCheckGenerator.{randomAlphaNumericStr, randomEmail, randomEmoji, randomTwoUpperCamelNouns, randomUSAddress, randomUSFirstName, randomUSLastName, randomUSPhoneNumber, randomUUID}
// import com.liyutech.common.data.codegen.ArbitraryProduct

import com.liyutech.common.USCommons.commonSingularNouns
import com.liyutech.common.{CommonUtil, TimeUtil, USCommons}

import com.liyutech.common.{CommonUtil, USCommons, USZipCodeInfo}
import com.liyutech.quill.model.{OrcaUser, Pod}
import java.util.{Base64, Date}
import scala.util.Random
import java.util.UUID
import com.liyutech.quill.model.PodAutokey

trait TestDataGen {
  protected val DefaultCitizenship = "US"

  // private lazy val searchStartDir = 
  private val projectName = "jvm-util"

  private def searchStartDir(): String = {
    val path = CommonUtil.currentClassPath()
    val i = path.indexOf(projectName)
    CommonUtil.findFirstMatchedRegularFile(path.substring(0, i), "USFirstNames.txt").fold("")(_.getParentFile.getAbsolutePath)
  }
  private lazy val rootPath = searchStartDir()
  protected def allUSZipcodeInfo: Array[USZipCodeInfo] = {
    USCommons.allUSZipcodeInfos(rootPath)
  }
  protected val allUSZipcodeInfoCount = allUSZipcodeInfo.length
  protected val earliestBirthdayFromNow = 365 * 100
  protected val earliestUpdatedAtTimeFromNow = 365
  protected val modelsPerIteration = 20

  def randomAlphaNumericStr(n: Int): String = scala.util.Random.shuffle(('A' to 'Z') ++ ('a' to 'z')).take(n).mkString
  def randomUUID(): String = UUID.randomUUID().toString
  def randomUSPhoneNumber(): String = s"${leftPadding(Random.nextInt(1000), 3)}-${leftPadding(Random.nextInt(1000), 3)}-${leftPadding(Random.nextInt(10000), 4)}"
  private def leftPadding(i: Int, fieldWidth: Int): String = s"%0${fieldWidth}d".format(i)
  private val allUSFirstNames = USCommons.usFirstNames(rootPath).size
  private val allUSLastNames = USCommons.usLastNames(rootPath).size
  private val allMiddleNameInitials = ' ' +: ('A' to 'Z')

  private val top10EmailDomainDistribution = Seq.fill(1774)("gmail.com") ++ Seq.fill(1734)("yahoo.com") ++ Seq.fill(1553)("hotmail.com") ++
    Seq.fill(320)("aol.com") ++ Seq.fill(127)("hotmail.co.uk") ++ Seq.fill(124)("hotmail.fr") ++
    Seq.fill(109)("msn.com") ++ Seq.fill(98)("yahoo.fr") ++ Seq.fill(90)("wanadoo.fr") ++ Seq.fill(83)("orange.fr")

  private val top10EmailDomainDistributionSize = top10EmailDomainDistribution.size


  def randomUSFirstName(): String = USCommons.usFirstNames(rootPath)(Random.nextInt(allUSFirstNames))
  def randomUSLastName(): String = USCommons.usLastNames(rootPath)(Random.nextInt(allUSLastNames))
  def randomUSMiddleNameInitial: String = allMiddleNameInitials(Random.nextInt(allMiddleNameInitials.length)).toString
  def randomUSFullName(delimiter: String = ""): String = s"${randomUSFirstName()}$delimiter${randomUSLastName()}"
  def randomEmailDomain: String = top10EmailDomainDistribution(Random.nextInt(top10EmailDomainDistributionSize))
  def randomEmail(account: String): String = s"$account@$randomEmailDomain"
  private lazy val CommonUSStreets = Seq.fill(10)("Main") ++ (0 to 100).map(CommonUtil.toOrdinal)
  private lazy val CommonUSStreetCount = CommonUSStreets.size
  private lazy val CommonGeoDirections = Seq("", "N", "S", "E", "W", "NE", "NW", "SE", "SW")
  private lazy val CommonGeoDirectionCount = CommonGeoDirections.size
  private lazy val CommonUSStreetAddressSuffixes = Seq("DR", "Drive", "Avenue", "Ave", "Boulevard", "CIR", "Road", "RD", "CT")
  private lazy val USStreetAddresses: Seq[String] = CommonUSStreetAddressSuffixes.flatMap(Seq.fill(100)(_)) ++ USCommons.usC1StreetSuffixes(rootPath)
  private lazy val USStreetAddressCount = USStreetAddresses.size

  def randomUSAddress: String = {
    Seq(Random.nextInt(99999),
      CommonGeoDirections(Random.nextInt(CommonGeoDirectionCount)),
      CommonUSStreets(Random.nextInt(CommonUSStreetCount)),
      USStreetAddresses(Random.nextInt(USStreetAddressCount))
    ).mkString(" ").replaceAll("\\s+", " ")
  }

  private lazy val _commonSingularNouns = commonSingularNouns(rootPath)
  private lazy val commonSingularNounCount = _commonSingularNouns.size
  def randomTwoUpperCamelNouns(delimiter: String = " "): String = {
    s"$randomTitleCaseNoun$delimiter$randomTitleCaseNoun"
  }

  lazy val randomTitleCaseNoun: String = 
    CommonUtil.firstLetterUppercase(_commonSingularNouns(Random.nextInt(commonSingularNounCount)))

  private lazy val commonEmojis = USCommons.commonEmojis(rootPath)
  private lazy val commonEmojiCount = commonEmojis.length
  private lazy val randomEmoji: String = commonEmojis(Random.nextInt(commonEmojiCount))

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

  protected def toReadablePodAutoKey(model: PodAutokey): PodAutokey = {
    val podDescription: String = randomTwoUpperCamelNouns()
    val podUserName: String = podDescription.toLowerCase.replaceAll(" ", ".")
    model.copy(
      podUsername = podUserName,
      podName = Option(podUserName),
      podDescription = Option(podDescription),
      emoji = model.emoji.map(_ => randomEmoji),
      updatedAt = CommonUtil.minusDays(new Date(), Random.nextLong(earliestUpdatedAtTimeFromNow))
    )
  }
}