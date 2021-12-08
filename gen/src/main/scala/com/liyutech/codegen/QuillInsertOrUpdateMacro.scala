package com.liyutech.codegen

import scala.reflect.macros.whitebox.{Context => MacroContext}

//https://github.com/getquill/quill-example/
class QuillInsertOrUpdateMacro(val c: MacroContext) {

  import c.universe._

  def insert[T](entity: Tree)(implicit t: WeakTypeTag[T]): Tree = {
    q"""
      import ${c.prefix}._
      val insertQuery = quote {
        query[$t].insert(lift($entity))
      }
      run(insertQuery)
      ()
    """
  }


  def insertOrUpdate[T](entity: Tree)(implicit t: WeakTypeTag[T]): Tree = {
    q"""
      import ${c.prefix}._
      val updateQuery = ${c.prefix}.quote {
        ${c.prefix}.query[$t].update(lift($entity))
      }
      val insertQuery = quote {
        query[$t].insert(lift($entity))
      }
      run(${c.prefix}.query[$t]).size match {
          case 1 => run(updateQuery)
          case _ => run(insertQuery)
      }
      ()
    """
  }

  def insertOrUpdateWithFilter[T](entity: Tree, filter: Tree)(implicit t: WeakTypeTag[T]): Tree = {
    q"""
      import ${c.prefix}._
      val updateQuery = ${c.prefix}.quote {
        ${c.prefix}.query[$t].filter($filter).update(lift($entity))
      }
      val insertQuery = quote {
        query[$t].insert(lift($entity))
      }
      run(${c.prefix}.query[$t].filter($filter)).size match {
          case 1 => run(updateQuery)
          case _ => run(insertQuery)
      }
      ()
    """
  }
}
