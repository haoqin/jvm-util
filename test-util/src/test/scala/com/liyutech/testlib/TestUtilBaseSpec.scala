package com.liyutech.testlib

import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}

trait TestUtilBaseSpec extends AsyncFlatSpec with Checkers with ScalaCheckPropertyChecks