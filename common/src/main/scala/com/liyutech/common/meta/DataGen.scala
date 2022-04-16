package com.liyutech.common.meta
import scala.compiletime.{constValue, erasedValue, summonInline, summonAll}
import scala.deriving.*
import scala.util.Random
import java.time.LocalDateTime

// case class User(name: String, age: Int, isActive: Boolean)
// type level metadata
// elemTypes: (String, Int, Boolean)
// elemLables: ("name", "age", "isActive")
// What if a case class is a field of another case class?
inline def summonAll[A <: Tuple]: List[RandomGen[_]] =
  inline erasedValue[A] match
    case _: EmptyTuple => List.empty[RandomGen[_]]
    case _: (h *: tail) => summonInline[RandomGen[h]] :: summonAll[tail]

private def toTuple(xs: List[_], acc: Tuple): Tuple =
  Tuple.fromArray(xs.toArray)

object DataGen {

//   private final case class NestedModel(s: String, b: Boolean, m: Model)
// private final case class Model(f: Float, d: LocalDateTime)

  inline given derived[T](using m: Mirror.Of[T]): RandomGen[T] =
    val instances = summonAll[m.MirroredElemTypes]
    inline m match
      case s: Mirror.SumOf[T]     => deriveSum(s, instances)
      case p: Mirror.ProductOf[T] => deriveProduct(p, instances)

  private def deriveProduct[T](p: Mirror.ProductOf[T], instances: => List[RandomGen[_]]): RandomGen[T] =
    () => p.fromProduct(toTuple(instances.map(g => g.generate()), EmptyTuple))


  //   // sum type, aka union type:
  // enum TrafficLight:
  //   case Red, Green, Orange
  private def deriveSum[T](p: Mirror.SumOf[T], instances: => List[RandomGen[_]]): RandomGen[T] =
    () => instances(Random.nextInt(instances.size)).asInstanceOf[RandomGen[T]].generate()

  inline def generateRandom[T](using m : Mirror.Of[T])(using RandomGen[T]): T = summon[RandomGen[T]].generate()
}