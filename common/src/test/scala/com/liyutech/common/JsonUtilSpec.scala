package com.liyutech.common

import io.circe.Json
import org.scalatest.flatspec.AsyncFlatSpec

class JsonUtilSpec extends AsyncFlatSpec {

  import JsonUtil._
  import io.circe.parser._

  "JsonUtil" should "validate simple value types" in {
    // A null field is considered a valid match against all json types. This is a practical compromise in that a js null
    // has no type information at all.
    val happyPaths = Json.Null.schemaMismatches(Json.Null).isEmpty &&
      Json.fromInt(1).schemaMismatches(Json.fromInt(1)).isEmpty &&
      Set(Json.Null).forall(Json.fromInt(1).schemaMismatches(_).isEmpty) &&
      Set(Json.fromInt(1), Json.fromBoolean(true)).forall(Json.Null.schemaMismatches(_).isEmpty)

    val errorPaths =  Set(Json.fromBoolean(true)).forall(Json.fromInt(1).schemaMismatches(_).nonEmpty)

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
    val sourceSchema = parse(CommonUtil.readFileAsOptionString("DynamicJsonSchema.json").fold("")(identity))
    val happyPath = for {
      schema <- sourceSchema
    } yield schema.schemaMismatches(schema)

    val mismatchedSchema = parse(CommonUtil.readFileAsOptionString("MismatchedJsonSchema.json").fold("")(identity))
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
