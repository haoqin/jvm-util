package com.liyutech.quill

import scala.reflect.macros.whitebox.{Context => MacroContext}

class QuillDeleteMacro(val c: MacroContext) {

  import c.universe._

  def delete[T](implicit t: WeakTypeTag[T]): Tree =
    q"""
      import ${c.prefix}._
      val q = ${c.prefix}.quote {
        ${c.prefix}.query[$t].delete
      }
      ${c.prefix}.run(q)
      ()
    """
}
