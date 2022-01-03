package com.liyutech.common

case class USZipCodeInfo(stateCode: String, city: String, state: String, zipCode: String)

object USCommons {
  private val UsFirstNameFileName = "USFirstNames.txt"
  private val NewLineDelimiter = "\n"
  val commonDir: String = CommonUtil.findFirstMatchedRegularFile(CommonUtil.CurrentClassPath(), UsFirstNameFileName).fold("") { file =>
    file.getParentFile.getAbsolutePath
  }
  //  val commonDir: String = CommonUtil.findFirstMatchDirectory(s"common/src/main/resources").fold("")(_.getAbsolutePath)
  val curriedReadFile = CommonUtil.readFileAsString(_: String, prefixPath = commonDir)
  lazy val usFirstNames: Seq[String] = curriedReadFile(UsFirstNameFileName).split(NewLineDelimiter)
  lazy val usLastNames: Seq[String] = curriedReadFile("USLastNames.txt").split(NewLineDelimiter)
  // a map from two-letter state codes to the corresponding state names.
  lazy val usStates: Map[String, String] = curriedReadFile("USStateCodes.txt")
    .split(NewLineDelimiter)
    .map { line =>
      val tokens = line.split(",")
      (tokens(0).replaceAll(""""""", ""), tokens(1))
    }.toMap

  lazy val allUSZipcodeInfos: Array[USZipCodeInfo] = curriedReadFile("USZipCodes.txt")
    .split(NewLineDelimiter)
    .map { line =>
      val tokens: Seq[String] = line.split("\t")
      val state = tokens(3)
      val stateCode = tokens(4).replaceAll("""""""", "")
      USZipCodeInfo(stateCode = stateCode, zipCode = s"%05d".format(tokens(1).toInt), city = tokens.apply(2), state = state)
    }

  lazy val usZipcodeInfo: Map[String, Array[USZipCodeInfo]] = allUSZipcodeInfos.groupBy(_.stateCode)

  import io.circe._
  import io.circe.parser._

  // 20220101, "USC1StreetSuffix.json" was copied from https://gist.github.com/mick-io/26db11e4c7f7aee6646b07d9f858eb9c.
  lazy val usC1StreetSuffixes: Seq[String] = {
    parse(curriedReadFile("USC1StreetSuffix.json")) match {
      case Left(error: ParsingFailure) => throw error
      case Right(obj: Json) => obj.asObject.map(o => o.keys).toSeq.flatten
    }
  }

  lazy val usCommonC1Suffixes: Seq[String] = Seq("AVE", "BLVD", "CT", "CV", "DR", "HWY", "LN", "PARK", "PKWY", "PL", "RD", "ST", "WAY")

  lazy val commonSingularNouns: Seq[String] = curriedReadFile("most-common-nouns-english.csv").split(NewLineDelimiter).map { line =>
    line.split(",").apply(0)
  }

  lazy val commonEmojis: Seq[String] = curriedReadFile("Emoji.txt").split(NewLineDelimiter)
}