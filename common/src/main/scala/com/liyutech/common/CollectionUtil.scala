package com.liyutech.common

object CollectionUtil {

  def findGroupMax[T, G, M: Ordering](groupBy: T => G, maxBy: T => M)(resultSet: Seq[T]): Seq[T] = {
    resultSet.groupBy(groupBy).map { case (_, values) =>
      val maxVal = values.map(maxBy).max
      values.collectFirst { case v if maxBy(v) == maxVal => v }.get
    }.toSeq
  }

  def split[T](seq: Seq[T], subseqSize: Int): Seq[Seq[T]] = {
    val maxSubseqIndex = seq.size / subseqSize
    (0 to maxSubseqIndex).map { i =>
      val startIndex = i * subseqSize
      val endIndex = startIndex + subseqSize
      seq.slice(startIndex, endIndex)
    }
  }

  implicit class ResultSetDecorator[T](resultSet: Seq[T]) {
    def findGroupMax[G, M: Ordering](groupBy: T => G, maxBy: T => M): Seq[T] = {
      CollectionUtil.findGroupMax[T, G, M](groupBy, maxBy)(resultSet)
    }

    def split(subseqSize: Int): Seq[Seq[T]] = {
      CollectionUtil.split(resultSet, subseqSize)
    }
  }

}
