package com.liyutech.common.meta

import com.liyutech.common.model.{OrcaUser, UserLoginModel, UserModel}
import org.scalatest.flatspec.AsyncFlatSpec
import com.liyutech.common.model.*
import scala.util.Random
import com.liyutech.quill.model.Pod

class DataGenSpec extends AsyncFlatSpec {
  "DataGen#RandomGen" should "generate random instances of product and coproducts" in {
    import DataGen.{given, *}
    import TypeUtil.*
    val userModel = generateRandom[UserModel]
    val orcaUser = generateRandom[OrcaUser]
    val pod = generateRandom[Pod]
    assert(userModel.isSubtypeOf[UserModel] && orcaUser.isInstanceOf[OrcaUser])
  }
}
