package com.liyutech.common.meta
import scala.compiletime.{constValue, erasedValue, summonInline, summonAll}

import scala.deriving.*
import scala.util.Random
import java.time.LocalDateTime

// https://medium.com/riskified-technology/type-class-derivation-in-scala-3-ba3c7c41d3ef
trait RandomGen[A]:
  def generate(): A

object RandomGen:
  given RandomGen[Int] with
    def generate() = Random.nextInt

  given RandomGen[Double] with
    def generate() = Random.nextDouble

  given RandomGen[String] with
    def generate() = Random.shuffle(('A' to 'Z') ++ ('a' to 'z')).take(Random.nextInt(16)).mkString

  given RandomGen[LocalDateTime] with
    def generate() = LocalDateTime.now
end RandomGen

inline def summonAll[A <: Tuple]: List[RandomGen[_]] =
  inline erasedValue[A] match
    case _: EmptyTuple => Nil
    case _: (t *: ts) => summonInline[RandomGen[t]] :: summonAll[ts]

def toTuple(xs: List[_], acc: Tuple): Tuple =
  xs match
    case Nil => acc
    case (h :: t) => h *: toTuple(t, acc)

object DataGen {
  inline given derived[A](using m: Mirror.Of[A]): RandomGen[A] =
    lazy val instances = summonAll[m.MirroredElemTypes]
    inline m match
      case s: Mirror.SumOf[A]     => deriveSum(s, instances)
      case p: Mirror.ProductOf[A] => deriveProduct(p, instances)


  private def deriveProduct[A](p: Mirror.ProductOf[A], instances: => List[RandomGen[_]]): RandomGen[A] = 
    new RandomGen[A]:
      def generate(): A = p.fromProduct(toTuple(instances.map(g => g.generate()), EmptyTuple))

  private def deriveSum[A](p: Mirror.SumOf[A], instances: => List[RandomGen[_]]): RandomGen[A] =
    new RandomGen[A]:
      def generate(): A =
        instances(Random.nextInt(instances.size)).asInstanceOf[RandomGen[A]].generate()

  inline def generateRandom[T](using m : Mirror.Of[T])(using RandomGen[T]): T = summon[RandomGen[T]].generate()
}