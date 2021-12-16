package com.liyutech.testlib.codegen

import scala.language.experimental.macros
import org.scalacheck.{Arbitrary, Gen}

object ArbitraryProduct {
  def arbitrary[T]: Gen[T] = macro ArbitraryProductMacro.arbitrary[T]
}
