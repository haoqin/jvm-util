// package com.liyutech.testlib

// import shapeless.Poly1

// import java.time.{LocalDate, LocalDateTime}

// object CleanData extends Poly1 {
//   implicit def stringCase(implicit f: String => String): Case.Aux[String, String] = at(str => Option(str).fold(str)(f))
//   implicit def optStringCase(implicit f: String => String): Case.Aux[Option[String], Option[String]] = at(str => str.map(f))
//   implicit val booleanCase: Case.Aux[Boolean, Boolean] = at(identity)
//   implicit val optBooleanCase: Case.Aux[Option[Boolean], Option[Boolean]] = at(identity)
//   implicit val intCase: Case.Aux[Int, Int] = at(identity)
//   implicit val longCase: Case.Aux[Long, Long] = at(identity)
//   implicit val floatCase: Case.Aux[Float, Float] = at(identity)
//   implicit val doubleCase: Case.Aux[Double, Double] = at(identity)
//   implicit val localDateCase: Case.Aux[LocalDate, LocalDate] = at(identity)
//   implicit val localDateTimeCase: Case.Aux[LocalDateTime, LocalDateTime] = at(identity)
// }