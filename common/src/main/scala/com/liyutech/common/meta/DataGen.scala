package com.liyutech.common.meta
import scala.compiletime.{constValue, erasedValue, summonInline, summonAll}

import scala.deriving.*
import scala.util.Random
import java.time.LocalDateTime

inline def summonAll[A <: Tuple]: List[RandomGen[_]] =
  inline erasedValue[A] match
    case _: EmptyTuple => Nil
    case _: (t *: ts) => summonInline[RandomGen[t]] :: summonAll[ts]

private def toTuple(xs: List[_], acc: Tuple): Tuple =
  Tuple.fromArray(xs.toArray)

object DataGen {
  inline given derived[A](using m: Mirror.Of[A]): RandomGen[A] =
    val instances = summonAll[m.MirroredElemTypes]
    inline m match
      case s: Mirror.SumOf[A]     => deriveSum(s, instances)
      case p: Mirror.ProductOf[A] => deriveProduct(p, instances)

  private def deriveProduct[A](p: Mirror.ProductOf[A], instances: => List[RandomGen[_]]): RandomGen[A] =
    () => p.fromProduct(toTuple(instances.map(g => g.generate()), EmptyTuple))

  private def deriveSum[A](p: Mirror.SumOf[A], instances: => List[RandomGen[_]]): RandomGen[A] =
    () => instances(Random.nextInt(instances.size)).asInstanceOf[RandomGen[A]].generate()

  inline def generateRandom[T](using m : Mirror.Of[T])(using RandomGen[T]): T = summon[RandomGen[T]].generate()
}