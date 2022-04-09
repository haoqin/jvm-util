package com.liyutech.common.meta
import scala.compiletime.{constValue, erasedValue, summonInline, summonAll}

import scala.deriving.Mirror

object TypeUtil {

  extension[E](e: E)
    inline def isSubtypeOf[S](using s: Mirror.SumOf[S]): Boolean = canBeAssignedTo[E, S]
  
  // Check if type E is a sub-type of a sum type S.
  inline def canBeAssignedTo[E, S](using s: Mirror.SumOf[S]): Boolean = isAssignable[E, s.MirroredElemTypes]

  private inline def isAssignable[E, S <: Tuple]: Boolean = inline erasedValue[S] match
    case _: EmptyTuple => false
    case _: (E *: t) => true
    case _: (_ *: t) => isAssignable[E, t] 

  inline def sumTypeCount[S](using s: Mirror.SumOf[S]): Int = summonSumTypeCount[s.MirroredElemTypes]

  private inline def summonSumTypeCount[T <: Tuple]: Int = inline erasedValue[T] match
    case _: EmptyTuple => 0
    case _: (h *: t) => 1 + summonSumTypeCount[t]

  extension [P <: Product](product: P)
    def isProjectionOf(anotherProduct: Product): Boolean =  (0 until product.productArity) forall { fieldIndex =>
      val fieldValue = product.productElement(fieldIndex)
      val fieldName = product.productElementName(fieldIndex)
      val anotherFieldIndex = anotherProduct.productElementNames.indexOf(fieldName)
      val anotherFieldValue = anotherProduct.productElement(anotherFieldIndex)
      fieldValue == anotherFieldValue
    }
  
  extension[P <: Product](p: P)
    inline def projectTo[S <: Product](using mS: Mirror.ProductOf[S]): S = {
    val projectedTuple = summonProjectable[P, mS.MirroredElemLabels, mS.MirroredElemTypes](p)
    mS.fromProduct(projectedTuple)
  }

  private inline def summonProjectable[L <: Product, N <: Tuple, T <: Tuple](m: L): Tuple = {
    val names = m.productElementNames.toSeq
    inline (erasedValue[N], erasedValue[T]) match
      case _: (EmptyTuple, EmptyTuple) => EmptyTuple
      case _: (EmptyTuple, _) => EmptyTuple
      case _: (_, EmptyTuple) => EmptyTuple
      case _: (nH *: nT, tH *: tT) => 
        val headName = constValue[nH].toString
        val nameIndex: Int = names.indexOf(headName)
        if (nameIndex < 0) {
          EmptyTuple 
        } else {
          val headValue = m.productElement(nameIndex)
          if (headValue.isInstanceOf[tH]) {
              headValue.asInstanceOf[tH] *: summonProjectable[L, nT, tT](m)
          }
          else {
            EmptyTuple 
          }
        }

  }
}