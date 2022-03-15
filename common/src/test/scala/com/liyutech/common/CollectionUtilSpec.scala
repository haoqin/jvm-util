package com.liyutech.common

import org.scalatest.flatspec.AsyncFlatSpec

class CollectionUtilSpec extends AsyncFlatSpec {

  "CollectionUtil.split" should "split a sequence into a sequence of non-overlapping sub-sequences" in {
    val resultSet = 0 to 110
    val subseqSize = 99

    import CollectionUtil._

    val subsequences: Seq[Seq[Int]] = resultSet.split(subseqSize)
    subsequences.foreach(println)
    assert {
      subsequences.forall(subSeq => subSeq.size <= subseqSize) &&
      // The start index of each subsequence has to be a multiple of subseqSize.
        subsequences.map(_.head).forall(head => head % subseqSize == 0)
    }
  }
}
