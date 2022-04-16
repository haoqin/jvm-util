package com.liyutech.common.meta

import com.liyutech.common.model.{OrcaUser, UserLoginModel, UserModel}
import org.scalatest.flatspec.AsyncFlatSpec
import com.liyutech.common.model.*
import scala.util.Random
import com.liyutech.quill.model.Pod
import java.time.LocalDateTime

private final case class NestedModel(s: String, b: Boolean, m: Model)
private final case class Model(f: Float, d: LocalDateTime)

// sum/union type:
enum TrafficLight:
  case Red, Green, Orange

class DataGenSpec extends AsyncFlatSpec {
  "DataGen#RandomGen" should "generate random instances of product types" in {
    import DataGen.{given, *}
    import TypeUtil.*
    val userModel = generateRandom[UserModel]
    val orcaUser = generateRandom[OrcaUser]
    val pod = generateRandom[Pod]
    assert(userModel.isSubtypeOf[UserModel] && orcaUser.isInstanceOf[OrcaUser])
  }

  "DataGen#RandomGen" should "generate random instances of sum types" in {
    import DataGen.{given, *}
    import TypeUtil.*
    val light = generateRandom[TrafficLight]
    println(s"light: $light")
    assert(light.isInstanceOf[TrafficLight])
  }

  "DataGen#RandomGen" should "generate random instances of a nested case class" in {
    import DataGen.{given, *}
    import TypeUtil.*
    val userModel = generateRandom[NestedModel]
    println(userModel)
    assert(userModel.isInstanceOf[NestedModel] && userModel.m.isInstanceOf[Model])
  }
  
}
