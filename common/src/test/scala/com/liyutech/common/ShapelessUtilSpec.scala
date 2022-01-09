package com.liyutech.common

import com.liyutech.common.model.{OrcaUser, UserLoginModel}
import org.scalatest.flatspec.AsyncFlatSpec

import java.time.LocalDateTime

class ShapelessUtilSpec extends AsyncFlatSpec {
  "ShapelessUtil ModelTransformer" should "support migration of a model to another with fewer fields" in {
    val orcaUser: OrcaUser = OrcaUser("803f-1d5h2AbE01W-4b4c-b561b-9ceb29841629b", "1d5h2AbE01W", "Mike Darell", "profile.png", "Mike", "Darell", LocalDateTime.parse("2001-05-11T01:21:24.99"), "ab@cde.de", "+12148366351", "US", None, None, None, None, None, None, LocalDateTime.now())
    import ShapelessUtil._
    val userLoginModel: UserLoginModel = orcaUser.projectTo[UserLoginModel]
    import com.liyutech.common.data.ProductUtil._
    assert(userLoginModel.isProjectionOf(orcaUser))
  }
}
