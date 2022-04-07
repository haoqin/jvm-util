// package com.liyutech.testlib

// import com.liyutech.common.data.ScalaCheckGenerator
// import org.scalacheck.{Arbitrary, Gen}

// class CleanDataSpec extends TestUtilBaseSpec with ScalaCheckGenerator {

//   private case class StringBoolean(s: String, b: Boolean)

//   private case class ComplexUser(id: String, uid: String, username: String, profileImage: String, firstName: String, lastName: String, dateOfBirth: java.time.LocalDateTime, emailAddress: String, phoneNumber: String, citizenship: String, personId: Option[String], streetLine_1: Option[String], streetLine_2: Option[String], city: Option[String], state: Option[String], postalCode: Option[String], updatedAt: java.time.LocalDateTime)

//   import org.scalacheck.ScalacheckShapeless.derivedArbitrary
//   private val stringBooleanGen: Gen[StringBoolean] = Arbitrary.arbitrary[StringBoolean]
//   private val userGen: Gen[ComplexUser] = Arbitrary.arbitrary[ComplexUser]

//   import com.liyutech.common.ShapelessUtil._

//   "CleanData" should "work with simple product" in forAll(stringBooleanGen) { model =>
//     val maxStrLen = 3
//     implicit val strTransform: String => String = _.take(maxStrLen)
//     val newModel: StringBoolean = model.map(CleanData)
//     assert(newModel.s.length <= maxStrLen)
//   }

//   "CleanData" should "work with complex product" in forAll(userGen) { model =>
//     import com.liyutech.common.ShapelessUtil._
//     implicit val strTransform: String => String = ScalaCheckGenerator.dbVarcharTransform
//     val newModel: ComplexUser = model.map(CleanData)
//     assert(newModel.id.length <= ScalaCheckGenerator.defaultMaxStrLen)
//   }

// }
