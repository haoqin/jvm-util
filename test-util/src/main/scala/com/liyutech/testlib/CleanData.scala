package com.liyutech.testlib

import shapeless.Poly1

import java.time.LocalDate

object CleanData extends Poly1 {
  implicit def stringCase(implicit f: String => String): Case.Aux[String, String] = at(str => Option(str).fold(str)(f))
  implicit def optStringCase(implicit f: String => String): Case.Aux[Option[String], Option[String]] = at(str => str.map(f))
  implicit val booleanCase: Case.Aux[Boolean, Boolean] = at(identity)
  implicit val intCase: Case.Aux[Int, Int] = at(identity)
  implicit val longCase: Case.Aux[Long, Long] = at(identity)
  implicit val localDateCase: Case.Aux[LocalDate, LocalDate] = at(identity)


}
