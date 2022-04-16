package com.liyutech.common.meta

import com.liyutech.common.model.{OrcaUser, UserLoginModel}
import org.scalatest.flatspec.AsyncFlatSpec

import java.time.LocalDateTime

// object TypeUtilSpec:
sealed trait SimpleTrait
case object A extends SimpleTrait
case class B() extends SimpleTrait

enum SimpleEnum:
  case E0, E1, E2, E3, E4

class TypeUtilSpec extends AsyncFlatSpec {

  private val orcaUserId = "803f-1d5h2AbE01W-4b4c-b561b-9ceb29841629b"
  private val orcaUserUid = "1d5h2AbE01W"
  private lazy val updatedAt = LocalDateTime.now()
  private val orcaUser: OrcaUser = OrcaUser(orcaUserId, orcaUserUid, "Mike Darell", "profile.png", "Mike", "Darell", LocalDateTime.parse("2001-05-11T01:21:24.99"), "ab@cde.de", "+12148366351", "US", None, None, None, None, None, None, updatedAt)
  
  "TypeUtil canBeAssignedTo" should "determione if a type belongs to the set of all implementatoins of the same sealed trait" in {
    import TypeUtil.*
    import SimpleEnum.*
    assert(canBeAssignedTo[B, SimpleTrait] && canBeAssignedTo[B, SimpleTrait] && canBeAssignedTo[SimpleEnum, SimpleEnum] && canBeAssignedTo[E3.type, SimpleEnum])
  }

  "TypeUtil sumTypeCount" should "count the number of sum types in an enum or the set of all implementatoins of the same sealed trait" in {
    import TypeUtil.*
    assert(sumTypeCount[SimpleTrait] == 2 && sumTypeCount[SimpleEnum] == 5)
  }

  "TypeUtil ModelTransformer" should "support migration of a model to another with fewer fields" in {
    import TypeUtil.*
    val userLoginModel: UserLoginModel = orcaUser.projectTo[UserLoginModel]
    println(s"userLoginModel\n$userLoginModel")
    assert(userLoginModel.isProjectionOf(orcaUser))
  }

  "TypeUtil extractField" should "extract a field with the given name and type from a case class instance" in {
    import TypeUtil.*
    assert(orcaUserId == orcaUser.extractField["id", String] && updatedAt == orcaUser.extractField["updatedAt", LocalDateTime])
  }
}
