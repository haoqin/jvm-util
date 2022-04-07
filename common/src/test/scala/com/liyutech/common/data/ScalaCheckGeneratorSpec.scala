// package com.liyutech.common.data

// import org.scalatest.flatspec.AsyncFlatSpec

// class ScalaCheckGeneratorSpec extends AsyncFlatSpec {

//   "ScalaCheckGenerator.randomUSPhoneNumber" should s"should generate zero padded US phone numbers" in {
//     assert {
//       (0 until 100) forall { _ =>
//         val phoneNumber = ScalaCheckGenerator.randomUSPhoneNumber()
//         println(s"phoneNumber $phoneNumber")
//         phoneNumber.length == 12
//       }
//     }
//   }

// }
