package com.liyutech.common.meta

import com.liyutech.common.model.{OrcaUser, UserLoginModel, UserModel}
import org.scalatest.flatspec.AsyncFlatSpec
import com.liyutech.common.model.*
import scala.util.Random

class DataGenSpec extends AsyncFlatSpec {
  "DataGen#RandomGen" should "generate random instances of prodcut and coproducts" in {
    import DataGen.{given, *}
    import TypeUtil.*
    val userModel = generateRandom[UserModel]
    val orcaUser = generateRandom[OrcaUser]
    assert(userModel.isSubtypeOf[UserModel] && orcaUser.isInstanceOf[OrcaUser])
  }
}
