package com.liyutech.common.meta
import scala.compiletime.{codeOf, constValue, erasedValue, error, summonAll, summonInline}
import scala.deriving.Mirror
import scala.runtime.stdLibPatches.language.experimental.namedTypeArguments
import org.scalacheck.Gen.Choose.IllegalBoundsError

import scala.compiletime.ops.int.{-, <, S}

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
  
  extension[P <: Product](p: P) {
    inline def projectTo[S <: Product](using m: Mirror.ProductOf[S]): S = {
      val projectedTuple = summonProjectable[P, m.MirroredElemLabels, m.MirroredElemTypes](p)
      m.fromProduct(projectedTuple)
    }
    // From the given product, extracts the singleton type F and returns the given type E.
    // Usage example: val updatedAt: LocalDateTime user.extractField["updatedAt", LocalDateTime]
    inline def extractField[F, E](using m: Mirror.ProductOf[P]): E =
      extractSingleField[m.MirroredElemLabels, m.MirroredElemTypes, F, E](Tuple.fromProduct(p))

    inline def extractField2[E](field: String)(using m: Mirror.ProductOf[P]): E =
      extractSingleField2[m.MirroredElemLabels, m.MirroredElemTypes, E](Tuple.fromProduct(p), field)
  }

  private inline def extractSingleField2[N <: Tuple, T <: Tuple, E](p: Tuple, field: String): E =
    inline (erasedValue[N], erasedValue[T]) match
      case _: (EmptyTuple, EmptyTuple) => error(codeOf(s"The tuple does not have the field $field"))
      case _: (nH *: nT, tH *: tT) =>
        // 20220416: What is confusing is that the boolean, t, is always true no matter where == or != is used.
        inline val t: Boolean = constValue[nH].toString == field
        if (t) {
          error(codeOf(s"The field type found -6: -->${field}<-->${constValue[nH].toString}<---"))
          inline erasedValue[nH] match
            case _: E =>
              error(codeOf("The field type found 0: ${field}"))
              p.productElement(0).asInstanceOf[E]
            case _ =>
              error(codeOf("The field type found 1: ${field}"))
              error(codeOf("The field type mismatch: ${field} - ${erasedValue[tH].isInstanceOf[E]}"))
        }
        else {
          error(codeOf(s"The field type not found 2: ${field}"))
          extractSingleField2[nT, tT, E](p.drop(1), field)
        }

  private inline def extractSingleField[N <: Tuple, T <: Tuple, F, E](p: Tuple): E =
    inline (erasedValue[N], erasedValue[T]) match
      case _: (EmptyTuple, EmptyTuple) => error("The tuple does not have the field")
      case _: (F *: nT, E *: tT) => p.productElement(0).asInstanceOf
      case _: (F *: nT, _ *: tT) => error("The field type does not match")
      case _: (_ *: nT, _ *: tT) => extractSingleField[nT, tT, F, E](p.drop(1))

  private inline def summonProjectable[L <: Product, N <: Tuple, T <: Tuple](m: L): Tuple = {
    val names = m.productElementNames.toSeq
    inline (erasedValue[N], erasedValue[T]) match
      case _: (EmptyTuple, EmptyTuple) | (EmptyTuple, _) | (_, EmptyTuple) => EmptyTuple
      case _: (nH *: nT, tH *: tT) =>
        val headName = constValue[nH].toString
        val nameIndex: Int = names.indexOf(headName)
        if nameIndex < 0 then EmptyTuple else {
          val headValue = m.productElement(nameIndex)
          if headValue.isInstanceOf[tH] then
            headValue.asInstanceOf[tH] *: summonProjectable[L, nT, tT](m)
          else EmptyTuple
        }
  }

  type <=[A <: Int, B <: Int] <: Boolean = A match
    case 0 => true
    case S[a] =>
      B match
        case 0 => false
        case S[b] => a <= b

  type %[A <: Int, B <: Int] <: Int = A < B match
    case true => A
    case _ => (A - B) % B
}