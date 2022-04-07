// package com.liyutech.common.data.codegen

// import scala.reflect.macros.whitebox.{Context => MacroContext}
// class ArbitraryProductMacro(val c: MacroContext) {

//   import c.universe._

//   def arbitrary[T](implicit t: WeakTypeTag[T]): Tree =
//     q"""
//       import org.scalacheck.Arbitrary.arbitrary
//       import org.scalacheck.GenAAr
//       import org.scalacheck.ScalacheckShapeless.derivedArbitrary
//       val productGen: Gen[$t] = arbitrary[$t]
//       productGen
//     """
// }