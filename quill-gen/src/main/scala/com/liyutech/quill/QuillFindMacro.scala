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
}
