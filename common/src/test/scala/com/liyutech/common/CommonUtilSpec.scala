package com.liyutech.common

import org.scalatest.flatspec.AsyncFlatSpec

class CommonUtilSpec extends AsyncFlatSpec {

  // DC has a state code too.
  // private val expectedUSStateCodes = 51

  "CommonUtil" should "toOrdinal " in {
    assert {
      CommonUtil.toOrdinal(102) == "102nd" &&
        CommonUtil.toOrdinal(203) == "203rd" &&
        CommonUtil.toOrdinal(6000) == "6000th"
    }
  }
}
