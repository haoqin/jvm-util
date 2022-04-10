package com.liyutech.common.meta
import scala.compiletime.{constValue, erasedValue, summonInline, summonAll}

import scala.deriving.*
import scala.util.Random
import java.time.LocalDateTime

// https://medium.com/riskified-technology/type-class-derivation-in-scala-3-ba3c7c41d3ef
trait RandomGen[A]:
  def generate(): A

object RandomGen:
  given RandomGen[Boolean] with
    def generate() = Random.nextBoolean
  given RandomGen[Byte] with 
    def generate() = Random.nextBytes(1).head

  given RandomGen[Char] with 
    def generate() = Random.nextPrintableChar

  given RandomGen[Int] with 
    def generate() = Random.nextInt

  given RandomGen[Long] with
    def generate() = Random.nextLong
  
  given RandomGen[Double] with
    def generate() = Random.nextDouble

  given RandomGen[Float] with
    def generate() = Random.nextFloat

  given RandomGen[String] with
    def generate() = Random.shuffle(('A' to 'Z') ++ ('a' to 'z')).take(Random.nextInt(16)).mkString

  given RandomGen[LocalDateTime] with
    def generate() = LocalDateTime.now.minusDays(Random.nextInt(365))
end RandomGen