package com.liyutech.common

import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json, JsonObject}
import shapeless.labelled.FieldType
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}

object HListCirceEncoder {
  implicit val hnilEncoder: Encoder[HNil] = Encoder.AsObject.instance[HNil](_ => JsonObject.empty)

  //
  //  implicit def listEncoder[H, T <: HList]()(implicit hEncoder: Lazy[Encoder[H]], tEncoder: Encoder[T]): Encoder.AsObject[H :: T] = Encoder.AsObject.instance[H :: T] { case (h :: t) =>
  //
  //    JsonObject.empty
  //  }

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
}


case class A(a: String)

case class B(b: Int)

case class C(c: Double)

case class SimpleCase(a: String, b: Int, cc: Boolean)

object ShapelessUtil {


  //  implicit val encodeHNil: ObjectEncoder[HNil] = ObjectEncoder.instance(_ => JsonObject.empty)
  //
  //  implicit def encodeHCons[H: ClassTag, T <: HList](implicit
  //    encodeH: Encoder[H],
  //    encodeT: ObjectEncoder[T],
  //    tag: ClassTag[H]
  //  ): ObjectEncoder[H :: T] = ObjectEncoder.instance {
  //    case h :: t => (tag.runtimeClass.getSimpleName.toLowerCase -> encodeH(h)) +: encodeT.encodeObject(t)
  //  }


  def main(args: Array[String]): Unit = {
    val gen = A("test") :: B(1) :: C(1.0) :: HNil
    val h = "22" :: 22 :: true :: HNil

    //    import io.circe.generic.auto._
    import io.circe.shapes._
    val hlist = LabelledGeneric[SimpleCase].to(SimpleCase("sssss", 2, true))

    println(hlist)

    val j = hlist.asJson
    println(s"=== ${j}")
  }

  ////  implicit val hnilEncoder: Encoder[JsonObject] = Encoder.asJsonObject((hnil: HNil) => JsonObject.empty)
  //  implicit val encodeHNil: Encoder[HNil] = (a: HNil) => Json.Null
  //
  //
  //  implicit def encodeHCons[A, H: Encoder, T <: HList](implicit
  //    encodeH: Encoder[H],
  //    gen: Generic.Aux[A, T],
  //    isHCons: IsHCons[H :: T]
  //  ): Encoder[H :: T] = new Encoder[H :: T] {
  //    override def apply(a: H :: T): Json = {
  //
  //      encodeH(a) +: encodeHCons
  //
  //      ???
  //    }
  //
  //  }


}
