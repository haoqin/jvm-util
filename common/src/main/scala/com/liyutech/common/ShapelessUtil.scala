package com.liyutech.common

import io.circe.{Encoder, Json, JsonObject}
import shapeless.labelled.FieldType
import shapeless.ops.hlist
import shapeless.{::, Generic, HList, HNil, Lazy, Poly, Witness}

trait ProductMapper[A, B, P] {
  def apply(a: A): B
}

object ShapelessUtil {
  implicit val hnilEncoder: Encoder[HNil] = Encoder.AsObject.instance[HNil](_ => JsonObject.empty)

  implicit def hlistEncoder[K <: Symbol, H, T <: HList](
                                                         implicit witness: Witness.Aux[K],
                                                         hEncoder: Lazy[Encoder[H]],
                                                         tEncoder: Encoder.AsObject[T]): Encoder.AsObject[FieldType[K, H] :: T] = {
    Encoder.AsObject.instance[FieldType[K, H] :: T] { instance =>

      val fieldName: String = witness.value.name
      val hValue: Json = hEncoder.value.apply(instance.head)
      val tValue: JsonObject = tEncoder.encodeObject(instance.tail)
      JsonObject(fieldName -> hValue).deepMerge(tValue)
    }
  }

  import shapeless.Poly1

  import java.time.{LocalDate, LocalDateTime}

  object CleanData extends Poly1 {
    implicit def stringCase(implicit f: String => String): Case.Aux[String, String] = at(str => Option(str).fold(str)(f))

    implicit def optStringCase(implicit f: String => String): Case.Aux[Option[String], Option[String]] = at(str => str.map(f))

    implicit val booleanCase: Case.Aux[Boolean, Boolean] = at(identity)
    implicit val optBooleanCase: Case.Aux[Option[Boolean], Option[Boolean]] = at(identity)
    implicit val intCase: Case.Aux[Int, Int] = at(identity)
    implicit val longCase: Case.Aux[Long, Long] = at(identity)
    implicit val floatCase: Case.Aux[Float, Float] = at(identity)
    implicit val doubleCase: Case.Aux[Double, Double] = at(identity)
    implicit val localDateCase: Case.Aux[LocalDate, LocalDate] = at(identity)
    implicit val localDateTimeCase: Case.Aux[LocalDateTime, LocalDateTime] = at(identity)
  }

  implicit class ProductMapperOps[A](a: A) {
    class Builder[B] {
      def apply[P <: Poly](poly: P)(implicit pm: ProductMapper[A, B, P]): B = pm.apply(a)
    }

    def map: Builder[A] = new Builder[A]
    def mapTo[B]: Builder[B] = new Builder[B]
  }

  implicit def genericProductMapper[
    A, B,
    P <: Poly,
    ARepr <: HList,
    BRepr <: HList
  ](
     implicit
     aGen: Generic.Aux[A, ARepr],
     bGen: Generic.Aux[B, BRepr],
     mapper: hlist.Mapper.Aux[P, ARepr, BRepr]
   ): ProductMapper[A, B, P] =
    (a: A) => bGen.from(mapper.apply(aGen.to(a)))
}
