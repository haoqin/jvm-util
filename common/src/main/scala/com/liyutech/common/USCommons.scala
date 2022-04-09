package com.liyutech.common

case class USZipCodeInfo(stateCode: String, city: String, state: String, zipCode: String)

object USCommons {
  private val UsFirstNameFileName = "USFirstNames.txt"
  private val NewLineDelimiter = "\n"
  val commonDir: String = CommonUtil.findFirstMatchedRegularFile(CommonUtil.currentClassPath(), UsFirstNameFileName).fold("") { file =>
    file.getParentFile.getAbsolutePath
  }
  def usFirstNames(pathPrefix: String = commonDir): Seq[String] = CommonUtil.readFileAsString(s"$pathPrefix/$UsFirstNameFileName").split(NewLineDelimiter)
  def usLastNames(pathPrefix: String = commonDir): Seq[String] = CommonUtil.readFileAsString(s"$pathPrefix/USLastNames.txt").split(NewLineDelimiter)
  // a map from two-letter state codes to the corresponding state names.
  def usStates(pathPrefix: String = commonDir): Map[String, String] = CommonUtil.readFileAsString(s"$pathPrefix/USStateCodes.txt").split(NewLineDelimiter)
    .map { line =>
      val tokens = line.split(",")
      (tokens(0).replaceAll(""""""", ""), tokens(1))
    }.toMap

  def allUSZipcodeInfos(pathPrefix: String = commonDir): Array[USZipCodeInfo] = CommonUtil.readFileAsString(s"$pathPrefix/USZipCodes.txt")
    .split(NewLineDelimiter)
    .map { line =>
      val tokens: Seq[String] = line.split("\t")
      val state = tokens(3)
      val stateCode = tokens(4).replaceAll("""""""", "")
      USZipCodeInfo(stateCode = stateCode, zipCode = s"%05d".format(tokens(1).toInt), city = tokens.apply(2), state = state)
    }

  def usZipcodeInfo(pathPrefix: String = commonDir): Map[String, Array[USZipCodeInfo]] = allUSZipcodeInfos(pathPrefix).groupBy(_.stateCode)

  import io.circe._
  import io.circe.parser._

  // 20220101, "USC1StreetSuffix.json" was copied from https://gist.github.com/mick-io/26db11e4c7f7aee6646b07d9f858eb9c.
  def usC1StreetSuffixes(pathPrefix: String = commonDir): Seq[String] = {
    parse(CommonUtil.readFileAsString(s"$pathPrefix/USC1StreetSuffix.json")) match {
      case Left(error: ParsingFailure) => throw error
      case Right(obj: Json) => obj.asObject.map(o => o.keys).toSeq.flatten
    }
  }

  val usCommonC1Suffixes: Seq[String] = Seq("AVE", "BLVD", "CT", "CV", "DR", "HWY", "LN", "PARK", "PKWY", "PL", "RD", "ST", "WAY")

  def commonSingularNouns(pathPrefix: String = commonDir): Seq[String] = CommonUtil.readFileAsString(s"$pathPrefix/most-common-nouns-english.csv").split(NewLineDelimiter).map { line =>
    line.split(",").apply(0)
  }
  

  def commonEmojis(pathPrefix: String = commonDir): Seq[String] = CommonUtil.readFileAsString(s"$pathPrefix/Emoji.txt").split(NewLineDelimiter)
}