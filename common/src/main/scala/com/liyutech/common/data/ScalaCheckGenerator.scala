// package com.liyutech.common.data

// import com.liyutech.common.USCommons.commonSingularNouns
// import com.liyutech.common.{CommonUtil, TimeUtil, USCommons}
// import org.scalacheck.{Arbitrary, Gen}

// import java.time.{LocalDate, LocalDateTime}
// import java.util.{Date, UUID}
// import scala.util.Random

// // The generator is a trait instead of object because some IDEs like Intellij will gray out imported implicits defined
// // in an object and therefore such imports often disappear after IDE organizes imports. Letting a type mix with such a trait does not have this IDE issue.
// trait ScalaCheckGenerator {
//   protected implicit val localDateGen: Gen[LocalDate] = Arbitrary.arbitrary[Date].map(TimeUtil.toLocalDate)
//   protected lazy implicit val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary(localDateGen)
//   protected implicit val localDateTimeGen: Gen[LocalDateTime] = {
//     Gen.calendar.map(_.getTime).map(TimeUtil.toLocalDateTime)
//     //    Arbitrary.arbitrary[Date].map(TimeUtil.toLocalDateTime)
//   }
//   protected lazy implicit val arbitraryLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary(localDateTimeGen)
// }

// object ScalaCheckGenerator {
//   val defaultMaxStrLen = 20
//   val uuidLen = 36
//   implicit val dbVarcharTransform: String => String = { str =>
//     //    val outputLen = Try(UUID.fromString(str)).toOption.fold(defaultMaxStrLen)(_ => uuidLen)
//     // UUID.randomUUID().toString.take(outputLen)
//     randomAlphaNumericStr(defaultMaxStrLen)
//   }

//   def randomAlphaNumericStr(n: Int): String = scala.util.Random.shuffle(('A' to 'Z') ++ ('a' to 'z')).take(n).mkString

//   def randomUUID(): String = UUID.randomUUID().toString

//   def randomUSPhoneNumber(): String = s"${leftPadding(Random.nextInt(1000), 3)}-${leftPadding(Random.nextInt(1000), 3)}-${leftPadding(Random.nextInt(10000), 4)}"

//   private def leftPadding(i: Int, fieldWidth: Int): String = s"%0${fieldWidth}d".format(i)

//   private val allUSFirstNames = USCommons.usFirstNames.size
//   private val allUSLastNames = USCommons.usLastNames.size
//   private val allMiddleNameInitials = ' ' +: ('A' to 'Z')

//   def randomUSFirstName(): String = USCommons.usFirstNames(Random.nextInt(allUSFirstNames))

//   def randomUSLastName(): String = USCommons.usLastNames(Random.nextInt(allUSLastNames))

//   def randomUSMiddleNameInitial: String = allMiddleNameInitials(Random.nextInt(allMiddleNameInitials.length)).toString

//   def randomUSFullName(delimiter: String = ""): String = s"$randomUSFirstName$delimiter$randomUSLastName"

//   // 20220101: https://email-verify.my-addr.com/list-of-most-popular-email-domains.php
//   //  1 gmail.com 17.74%
//   //  2   yahoo.com 17.34%
//   //  3   hotmail.com 15.53%
//   //  4   aol.com 3.2%
//   //  5   hotmail.co.uk 1.27%
//   //  6   hotmail.fr  1.24%
//   //  7   msn.com 1.09%
//   //  8   yahoo.fr  0.98%
//   //  9   wanadoo.fr  0.9%
//   //  10    orange.fr 0.83%

//   private val top10EmailDomainDistribution = Seq.fill(1774)("gmail.com") ++ Seq.fill(1734)("yahoo.com") ++ Seq.fill(1553)("hotmail.com") ++
//     Seq.fill(320)("aol.com") ++ Seq.fill(127)("hotmail.co.uk") ++ Seq.fill(124)("hotmail.fr") ++
//     Seq.fill(109)("msn.com") ++ Seq.fill(98)("yahoo.fr") ++ Seq.fill(90)("wanadoo.fr") ++ Seq.fill(83)("orange.fr")

//   private val top10EmailDomainDistributionSize = top10EmailDomainDistribution.size

//   def randomEmailDomain: String = top10EmailDomainDistribution(Random.nextInt(top10EmailDomainDistributionSize))

//   def randomEmail(account: String): String = s"$account@$randomEmailDomain"

//   private lazy val CommonUSStreets = Seq.fill(10)("Main") ++ (0 to 100).map(CommonUtil.toOrdinal)
//   private lazy val CommonUSStreetCount = CommonUSStreets.size
//   private lazy val CommonGeoDirections = Seq("", "N", "S", "E", "W", "NE", "NW", "SE", "SW")
//   private lazy val CommonGeoDirectionCount = CommonGeoDirections.size

//   private lazy val CommonUSStreetAddressSuffixes = Seq("DR", "Drive", "Avenue", "Ave", "Boulevard", "CIR", "Road", "RD", "CT")
//   private lazy val USStreetAddresses: Seq[String] = CommonUSStreetAddressSuffixes.flatMap(Seq.fill(100)(_)) ++ USCommons.usC1StreetSuffixes
//   private lazy val USStreetAddressCount = USStreetAddresses.size

//   def randomUSAddress: String = {
//     Seq(Random.nextInt(99999),
//       CommonGeoDirections(Random.nextInt(CommonGeoDirectionCount)),
//       CommonUSStreets(Random.nextInt(CommonUSStreetCount)),
//       USStreetAddresses(Random.nextInt(USStreetAddressCount))
//     ).mkString(" ").replaceAll("\\s+", " ")
//   }

//   private lazy val commonSingularNounCount = commonSingularNouns.size
//   def randomTwoUpperCamelNouns(delimiter: String = " "): String = {
//     s"$randomTitleCaseNoun$delimiter$randomTitleCaseNoun"
//   }

//   def randomTitleCaseNoun: String = {
//     CommonUtil.firstLetterUppercase(USCommons.commonSingularNouns(Random.nextInt(commonSingularNounCount)))
//   }

//   private lazy val commonEmojiCount = USCommons.commonEmojis.length
//   def randomEmoji: String = USCommons.commonEmojis(Random.nextInt(commonEmojiCount))
// }
