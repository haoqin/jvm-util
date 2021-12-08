package com.liyutech.quill

import io.getquill.context.jdbc.JdbcContext
import io.getquill.ActionReturning

import scala.language.experimental.macros

// Inspired by the examples from https://github.com/getquill/quill-example/tree/4723a5e482efb75b04371ad1d6410219b0893364
trait QuillGenericDao { this : JdbcContext[_, _] =>

  def find[T](filter: (T) => Boolean): List[T] = macro QuillFindMacro.find[T]
  def findAll[T]: List[T] = macro QuillFindMacro.findAll[T]

  def deleteAll[T]: Unit = macro QuillDeleteMacro.delete[T]

  def insert[T](entity: T): Long = macro QuillInsertOrUpdateMacro.insert[T]
  def insertWithReturn[T, R](entity: T, extractPrimaryKey: (T) => R): R = macro QuillInsertOrUpdateMacro.insertWithReturn[T, R]

  def insertOrUpdate[T](entity: T): Unit = macro QuillInsertOrUpdateMacro.insertOrUpdate[T]
  def insertOrUpdateWithFilter[T](entity: T, filter: (T) => Boolean = (_: T) => true): Unit = macro QuillInsertOrUpdateMacro.insertOrUpdateWithFilter[T]

}