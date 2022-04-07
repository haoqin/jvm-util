// package com.liyutech.common

// import shapeless.ops.hlist
// import shapeless.{Generic, HList, Poly}

// trait ProductMapper[A, B, P] {
//   def apply(a: A): B
// }

// // 20211215: Copied from the "Type Astronaut's Guide to Shapeless".
// object ShapelessUtil {
//   implicit class ProductMapperOps[A](a: A) {
//     class Builder[B] {
//       def apply[P <: Poly](poly: P)(implicit pm: ProductMapper[A, B, P]): B = pm.apply(a)
//     }

//     def map: Builder[A] = new Builder[A]
//     def mapTo[B]: Builder[B] = new Builder[B]
//   }

//   implicit def genericProductMapper[
//     A, B,
//     P <: Poly,
//     ARepr <: HList,
//     BRepr <: HList
//   ](
//      implicit
//      aGen: Generic.Aux[A, ARepr],
//      bGen: Generic.Aux[B, BRepr],
//      mapper: hlist.Mapper.Aux[P, ARepr, BRepr]
//    ): ProductMapper[A, B, P] =
//     (a: A) => bGen.from(mapper.apply(aGen.to(a)))
// }
