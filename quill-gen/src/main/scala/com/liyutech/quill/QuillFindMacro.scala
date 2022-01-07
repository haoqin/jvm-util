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

  def findBy[T, I](id: Tree, extractId: Tree)(implicit t: WeakTypeTag[T], i: WeakTypeTag[I]): Tree =
    q"""
      import ${c.prefix}._
      val q = ${c.prefix}.quote {
        ${c.prefix}.query[$t].filter { model =>
          val modelId: $i = $extractId(model)
          modelId == lift($id)
        }
      }
      val entities: Seq[$t] = ${c.prefix}.run(q)
      entities
    """

  def findMax[T, I, M](id: Tree, extractId: Tree, maxBy: Tree)(implicit t: WeakTypeTag[T], i: WeakTypeTag[I], m:WeakTypeTag[M]): Tree =
    q"""
      import ${c.prefix}._
      val q = ${c.prefix}.quote {
        ${c.prefix}.query[$t].filter { model =>
          val modelId: $i = $extractId(model)
          modelId == lift($id)
        }
      }
      val entities: Seq[$t] = ${c.prefix}.run(q)
      entities.maxBy($maxBy)
    """

  def findMin[T, I, M](id: Tree, extractId: Tree, minBy: Tree)(implicit t: WeakTypeTag[T], i: WeakTypeTag[I], m:WeakTypeTag[M]): Tree =
    q"""
      import ${c.prefix}._
      val q = ${c.prefix}.quote {
        ${c.prefix}.query[$t].filter { model =>
          val modelId: $i = $extractId(model)
          modelId == lift($id)
        }
      }
      val entities: Seq[$t] = ${c.prefix}.run(q)
      entities.minBy($minBy)
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
  def findGroupMax[T, G, M](groupBy: Tree, maxBy: Tree)(filter: Tree)(implicit t: WeakTypeTag[T], g: WeakTypeTag[G], m:WeakTypeTag[M]): Tree =
    q"""
      import ${c.prefix}._
      val q0 = ${c.prefix}.quote {
        ${c.prefix}.query[$t].groupBy[$g]($groupBy).map { case (groupById, models) =>
          val m: $m = models.map($maxBy).max.orNull
          (groupById, m)
        }
      }
      val entities: Seq[($g, $m)] = ${c.prefix}.run(q0)
      entities.flatMap { case(groupById, m) =>
        val q = ${c.prefix}.quote {
          ${c.prefix}.query[$t].filter { model => $filter(model, lift(groupById), lift(m)) }
        }
        ${c.prefix}.run(q)
      }
    """


//  def findGroup2Max[T](tableName: Tree, maxBy: Tree, groupByField0: Tree, groupByField1: Tree)(implicit t: WeakTypeTag[T]): Tree =
//    q"""
//      import ${c.prefix}._
//      ${c.prefix}.run(quote(infix"SELECT * from #$tableName".as[Query[$t]]))
//    """
}
