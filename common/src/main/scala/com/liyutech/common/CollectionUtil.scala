package com.liyutech.common

object CollectionUtil {

  def findGroupMax[T, G, M: Ordering](groupBy: T => G, maxBy: T => M)(resultSet: Seq[T]): Seq[T] = {
    resultSet.groupBy(groupBy).map { case (_, values) =>
      val maxVal = values.map(maxBy).max
      values.collectFirst { case v if maxBy(v) == maxVal => v }.get
    }.toSeq
  }

  implicit class ResultSetDecorator[T](resultSet: Seq[T]) {
    def findGroupMax[G, M: Ordering](groupBy: T => G, maxBy: T => M): Seq[T] = {
      CollectionUtil.findGroupMax[T, G, M](groupBy, maxBy)(resultSet)
    }
  }

}
