package com.liyutech.testlib

import org.scalacheck.{Arbitrary, Gen}

class CleanDataSpec extends TestUtilBaseSpec with ScalaCheckGenerator {

  private case class StringBoolean(s: String, b: Boolean)

  private case class ComplexUser(id: String, uid: String, profileImage: String, firstName: String, lastName: String, dateOfBirth: java.time.LocalDate, emailAddress: String, phoneNumber: String, citizenship: String, personId: String, streetLine1: String, streetLine2: Option[String], city: String, state: String, postalCode: String, updatedAt: java.time.LocalDate)

  import org.scalacheck.ScalacheckShapeless.derivedArbitrary
  private val stringBooleanGen: Gen[StringBoolean] = Arbitrary.arbitrary[StringBoolean]
  private val userGen: Gen[ComplexUser] = Arbitrary.arbitrary[ComplexUser]

  import com.liyutech.testlib.ShapelessUtil._

  "CleanData" should "work with simple product" in forAll(stringBooleanGen) { model =>
    val maxStrLen = 3
    implicit val strTransform: String => String = _.take(maxStrLen)
    val newModel: StringBoolean = model.map(CleanData)
    assert(newModel.s.length <= maxStrLen)
  }

  "CleanData" should "work with complex product" in forAll(userGen) { model =>
    import com.liyutech.testlib.ShapelessUtil._
    implicit val strTransform: String => String = ScalaCheckGenerator.dbVarcharTransform
    val newModel: ComplexUser = model.map(CleanData)
    assert(newModel.id.length <= ScalaCheckGenerator.defaultMaxStrLen)
  }

}
