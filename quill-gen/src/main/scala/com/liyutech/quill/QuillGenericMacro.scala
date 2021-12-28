package com.liyutech.quill

import io.getquill.context.jdbc.JdbcContext
import io.getquill.ActionReturning

import scala.language.experimental.macros
import scala.math.Ordering

// Inspired by the examples from https://github.com/getquill/quill-example/tree/4723a5e482efb75b04371ad1d6410219b0893364
trait QuillGenericMacro {
  this: JdbcContext[_, _] =>

  def find[T](filter: (T) => Boolean): Seq[T] = macro QuillFindMacro.find[T]

  def findAll[T]: Seq[T] = macro QuillFindMacro.findAll[T]

  def findMaxFields[T, G, M](groupBy: T => G, maxBy: T => M): Seq[(G, M)] = macro QuillFindMacro.findMaxFields[T, G, M]

  // Find max for each group.
  def findGroupMax[T, G, M](groupBy: T => G, maxBy: T => M)(filter: (T, G, M) => Boolean): Seq[T] = macro QuillFindMacro.findGroupMax[T, G, M]

  // Find by id and max:
  def findMax[T, I, M](id: I, extractId: T => I, maxBy: T => M): Option[T] = macro QuillFindMacro.findMax[T, I, M]

  def deleteAll[T]: Unit = macro QuillDeleteMacro.delete[T]

  def insert[T](entity: T): Long = macro QuillInsertOrUpdateMacro.insert[T]

  // Insert all models in a transactional manner, returning 0 or 1 if the insertion fails or succeeds, respectively.
  def insertAll[T](entities: Seq[T]): Seq[Long] = macro QuillInsertOrUpdateMacro.insertAll[T]

  // Insert all models in a transactional manner, using the given function to extract the key, R, from the model, T.
  def insertAllAutoIncremented[T, R](entities: Seq[T], extractPrimaryKey: (T) => R): Seq[R] = macro QuillInsertOrUpdateMacro.insertAllAutoIncremented[T, R]

  def insertAutoIncremented[T, R](entity: T, extractPrimaryKey: (T) => R): R = macro QuillInsertOrUpdateMacro.insertAutoIncremented[T, R]

  def insertOrUpdate[T](entity: T): Unit = macro QuillInsertOrUpdateMacro.insertOrUpdate[T]

  def insertOrUpdateWithFilter[T](entity: T, filter: (T) => Boolean = (_: T) => true): Unit = macro QuillInsertOrUpdateMacro.insertOrUpdateWithFilter[T]

}