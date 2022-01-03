package com.liyutech.common.data.codegen

import org.scalacheck.Gen
import scala.language.experimental.macros

object ArbitraryProduct {
  def arbitrary[T]: Gen[T] = macro ArbitraryProductMacro.arbitrary[T]
}
