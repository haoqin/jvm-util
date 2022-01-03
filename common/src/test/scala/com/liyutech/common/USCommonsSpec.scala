package com.liyutech.common

import org.scalatest.flatspec.AsyncFlatSpec

class USCommonsSpec extends AsyncFlatSpec {

  // DC has a state code too.
  private val expectedUSStateCodes = 51

  "USCommons" should s"has $expectedUSStateCodes state codes" in {
    assert(USCommons.usStates.size == expectedUSStateCodes)
  }

  "USCommons" should s"has matching state codes" in {
    val stateCodesFromZipCodeInfo = USCommons.usZipcodeInfo.keys.toSet
    val stateCodesFromStateCodes = USCommons.usStates.keys.toSet
    val unmatched = stateCodesFromStateCodes.filterNot(stateCodesFromZipCodeInfo)
    assert(unmatched.isEmpty)
  }
}
