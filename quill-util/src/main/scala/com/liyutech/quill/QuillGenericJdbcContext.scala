package com.liyutech.quill

import io.getquill.context.jdbc.JdbcContext
import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.parser.engine.Parser

import scala.quoted.*
import io.getquill.ast.AggregationOperator.max

object QuillGenericJdbcContext {
  // implicit classes have an advantage over extension methods in that the former allow import statements outside of
  // any method.
  implicit class JdbcContextDecorator[D <: SqlIdiom, N <: NamingStrategy](dao: JdbcContext[D, N]) {
    import dao.*
    // Using Scala 3, the find[T].filter function works for h2 but not postgres. It worked well with Scala 2 macro.
    // inline def find[T](filter: T => Boolean): Seq[T] = run(query[T].filter(filter))
    inline def findBy[T, I](id: I, extractId: T => I): Seq[T] = run(query[T].filter(m => extractId(m) == lift(id)))
    inline def findAll[T]: Seq[T] = run(query[T])

    // Find max by id and max:
    inline def findMax[T, I, M:Ordering](id: I, extractId: T => I, maxBy: T => M): Option[T] =
      findBy[T, I](id, extractId).maxByOption(maxBy)

    inline def findGroupMax[T, G, M:Ordering](groupBy: T => G, maxBy: T => M): Seq[T] = 
      findAll[T].groupBy(groupBy).map((groupId, models) => models.maxBy(maxBy)).toSeq

    inline def deleteAll[T]: Long = run(query[T].delete)

    inline def insert[T](entity: T): Long = run(query[T].insertValue(lift(entity)))
    inline def insertAll[T](entities: Seq[T]): Seq[Long] = run {
      liftQuery(entities).foreach(c => query[T].insertValue(c))
    }

    inline def insertAllAutoIncremented[T, R](entities: Seq[T], extractPrimaryKey: T => R): Seq[R] = run {
      liftQuery(entities).foreach(c => query[T].insertValue(c).returningGenerated(extractPrimaryKey))
    }

    inline def autoKeyAfterInsert[T, R](entity: T, extractPrimaryKey: T => R): R  = run(autoKeyAfterInsertAction(entity, extractPrimaryKey))

    inline def autoKeysAfterInsert[T, R](entities: Seq[T], extractPrimaryKey: T => R): Seq[R] = transaction[Seq[R]] {
      entities.map(entity => run(autoKeyAfterInsertAction(entity, extractPrimaryKey)))
    }

    inline def autoKeyAfterInsertAction[T, R](entity: T, extractPrimaryKey: T => R): ActionReturning[T, R] = 
      query[T].insertValue(lift(entity)).returningGenerated(extractPrimaryKey)

  }
}