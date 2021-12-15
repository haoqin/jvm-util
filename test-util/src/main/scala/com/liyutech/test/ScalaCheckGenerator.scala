package com.liyutech.test

import com.liyutech.common.TimeUtil
import org.scalacheck.{Arbitrary, Gen}

import java.time.LocalDate
import java.util.Date

// The generator is a trait instead of object because some IDEs like Intellij will gray out imported implicits defined
// in an object and therefore such imports often disappear after IDE organizes imports. Letting a type mix with such a trait does not have this IDE issue.
trait ScalaCheckGenerator {
  protected implicit val localDateGen: Gen[LocalDate] = Arbitrary.arbitrary[Date].map(TimeUtil.toLocalDate)
  protected lazy implicit val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary(localDateGen)
  protected def randomAlphaNumericStr(n: Int): String = scala.util.Random.shuffle(('A' to 'Z') ++ ('a' to 'z')).take(n).mkString
}
