package com.liyutech.quill

import scala.reflect.macros.whitebox.{Context => MacroContext}

class QuillFindMacro(val c: MacroContext) {

  import c.universe._

  def find[T](filter: Tree)(implicit t: WeakTypeTag[T]): Tree =
    q"""
      import ${c.prefix}._
      val q = ${c.prefix}.quote {
        ${c.prefix}.query[$t].filter($filter)
      }
      ${c.prefix}.run(q)
    """

  def findAll[T](implicit t: WeakTypeTag[T]): Tree =
    q"""
      import ${c.prefix}._
      val q = ${c.prefix}.quote {
        ${c.prefix}.query[$t]
      }
      ${c.prefix}.run(q)
    """

  def findMaxFields[T, G, M](groupBy: Tree, maxBy: Tree)(implicit t: WeakTypeTag[T], g: WeakTypeTag[G], m:WeakTypeTag[M]): Tree =
    q"""
      import ${c.prefix}._
      val q = ${c.prefix}.quote {
        ${c.prefix}.query[$t].groupBy[$g]($groupBy).map { case (groupById, models) =>
          val m: $m = models.map($maxBy).max.orNull
          (groupById, m)
        }
      }
      ${c.prefix}.run(q)
    """
  def findMax[T, G, M](groupBy: Tree, maxBy: Tree)(filter: Tree)(implicit t: WeakTypeTag[T], g: WeakTypeTag[G], m:WeakTypeTag[M]): Tree =
    q"""
      import ${c.prefix}._
      val q0 = ${c.prefix}.quote {
        ${c.prefix}.query[$t].groupBy[$g]($groupBy).map { case (groupById, models) =>
          val m: $m = models.map($maxBy).max.orNull
          (groupById, m)
        }
      }
      val entities: Seq[($g, $m)] = ${c.prefix}.run(q0)
      entities.flatMap { case(gv, sv) =>
        val q = ${c.prefix}.quote {
          ${c.prefix}.query[$t].filter { m => $filter(m, lift(gv), lift(sv)) }
        }
        ${c.prefix}.run(q)
      }
    """
}
