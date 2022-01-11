package com.liyutech.common

import com.liyutech.common.CommonUtil.TestResourceDir
import io.circe.Json
import org.scalatest.flatspec.AsyncFlatSpec

class JsonUtilSpec extends AsyncFlatSpec {

  import JsonUtil._
  import io.circe.parser._

  "JsonUtil" should "validate simple value types" in {
    val happyPaths = Json.Null.schemaMismatches(Json.Null).isEmpty &&
      Json.fromInt(1).schemaMismatches(Json.fromInt(1)).isEmpty
    val errorPaths = Set(Json.Null, Json.fromBoolean(true)).forall(Json.fromInt(1).schemaMismatches(_).nonEmpty) &&
      Set(Json.fromInt(1), Json.fromBoolean(true)).forall(Json.Null.schemaMismatches(_).nonEmpty)

    assert(happyPaths && errorPaths)
  }

  "JsonUtil" should "validate simple json objects" in {

    val happyPath = for {
      obj <- parse("""{"a": 1}""")
      matchedObj <- parse("""{"a": 10}""")
    } yield obj.schemaMismatches(matchedObj)

    val simpleErrorPath = for {
      obj <- parse("""{"a": 1}""")
      matchedObj <- parse("""{"b": 10}""")
    } yield obj.schemaMismatches(matchedObj)
    val expectedErrors: Set[String] = Set("Missing key: a", "Unexpected new key: b")
    assert(happyPath.map(_.isEmpty).isRight && simpleErrorPath.map(_ == expectedErrors) == Right(true))
  }

  "JsonUtil" should "validate complex json objects" in {
    val sourceSchema = parse(CommonUtil.readFileAsString("DynamicJsonSchema.json", TestResourceDir.replace("jvm-util", "jvm-util/common")))
    val happyPath = for {
      schema <- sourceSchema
    } yield schema.schemaMismatches(schema)

    val mismatchedSchema = parse(CommonUtil.readFileAsString("MismatchedJsonSchema.json", TestResourceDir.replace("jvm-util", "jvm-util/common")))
    val errorPath = for {
      schema <- sourceSchema
      mismatched <- mismatchedSchema
    } yield schema.schemaMismatches(mismatched)

    println(s"errorPath count: ${errorPath.map(_.size)}\n$errorPath")

    // The number of errors compare two schemas should always be an even number because the errors are reciprocal.
    val assertEvenErrorSizes: Set[String] => Boolean = errors => errors.nonEmpty && errors.size % 2 == 0
    assert(happyPath.map(_.isEmpty) == Right(true) && errorPath.map(assertEvenErrorSizes) == Right(true))
  }

}
