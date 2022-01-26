package com.liyutech.common

import org.scalatest.flatspec.AsyncFlatSpec

import java.io.File

class CommonUtilSpec extends AsyncFlatSpec {

  // DC has a state code too.
  // private val expectedUSStateCodes = 51

  "CommonUtil" should "toOrdinal " in {
    assert {
      CommonUtil.toOrdinal(102) == "102nd" &&
        CommonUtil.toOrdinal(203) == "203rd" &&
        CommonUtil.toOrdinal(6000) == "6000th"
    }
  }

  "CommonUtil" should "findFirstMatchedRegularFile " in {
    val fileName = "Emoji.txt"
    val optFile: Option[File] = CommonUtil.findFirstMatchedRegularFile(".", fileName)
    assert {
      optFile.fold(false) { file =>
        val path: String = file.getCanonicalPath
        path.endsWith(fileName)

      }
    }
  }

  "CommonUtil" should "readAsOptionString " in {
    val fileName = "Emoji.txt"
    val optFileContent: Option[String] = CommonUtil.readFileAsOptionString(fileName = fileName)
    assert {
      optFileContent.fold(false)(_.nonEmpty)
    }
  }
}
