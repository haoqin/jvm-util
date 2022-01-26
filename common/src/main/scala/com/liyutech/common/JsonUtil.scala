package com.liyutech.common

import io.circe.{Json, JsonObject}

object JsonUtil {

  implicit class Tuple2SeqDecorator[A, B](input: Seq[(A, B)]) {
    def toJson(fn: (A, B) => Json): Json = tuple2ToJson(input)(fn)
  }

  def tuple2ToJson[A, B](input: Seq[(A, B)])(fn: (A, B) => Json): Json = {
    Json.fromValues(input.map { case (a, b) => fn(a, b) })
  }

  def jsonType(json: Json): String = {
    if (json.isObject) {
      "object"
    }
    else if (json.isArray) {
      "array"
    }
    else if (json.isBoolean) {
      "boolean"
    }
    else if (json.isString) {
      "string"
    }
    else if (json.isNumber) {
      "number"
    }
    else if (json.isNull) {
      "null"
    }
    else {
      "unknown"
    }
  }


  implicit class DynamicJsonSchemaValidator(json: Json) {
    private def schemaMismatches(matchedKeys: Set[String], obj: JsonObject, anotherObj: JsonObject): Set[String] = {
      matchedKeys.flatMap { key =>
        // Option.get is guaranteed to be safe here.
        obj(key).get.schemaMismatches(anotherObj(key).get)
      }
    }

    private def schemaMismatches(matchedKeys: Set[String], newKeys: Set[String], obj: JsonObject, anotherObj: JsonObject): Set[String] = {
      matchedKeys.flatMap { key =>
        // Option.get is guaranteed to be safe here because the keys are shared by both objects.
        obj(key).get.schemaMismatches(anotherObj(key).get)
      }
    }

    // Compare the schemata of two Json values and return all mismatches. Since JSON only has a small set of data types,
    // the comparison will be not complete. For example, LocalDateTime is often expressed as a JSON string, so the
    // schema comparison against LocalDateTime against string will reveal no difference.
    def schemaMismatches(anotherJson: Json): Set[String] = {
      if (json.isBoolean) {
        if (anotherJson.isBoolean || anotherJson.isNull) {
          Set.empty[String]
        }
        else {
          Set(s"A boolean does not match ${jsonType(anotherJson)}")
        }
      }
      else if (json.isNumber) {
        if (anotherJson.isNumber || anotherJson.isNull) {
          Set.empty[String]
        }
        else {
          Set(s"A number does not match ${jsonType(anotherJson)}")
        }
      }
      else if (json.isString) {
        if (anotherJson.isString || anotherJson.isNull) {
          Set.empty[String]
        }
        else {
          Set(s"A string does not match ${jsonType(anotherJson)}")
        }
      }
      else if (json.isNull) {
        //        if (anotherJson.isNull) {
        Set.empty[String]
        //        }
        //        else {
        //          Set(s"A null type does not match ${jsonType(anotherJson)}")
        //        }
      }
      else if (json.isObject) {
        if (anotherJson.isObject) {
          val obj = json.asObject.get
          val anotherObj = anotherJson.asObject.get
          val objKeys: Set[String] = obj.keys.toSet
          val anotherObjKeys: Set[String] = anotherObj.keys.toSet
          val matchedKeys: Set[String] = objKeys.intersect(anotherObjKeys)
          val missingKeys: Set[String] = objKeys.filterNot(anotherObjKeys)
          val newKeys: Set[String] = anotherObjKeys.filterNot(objKeys)
          schemaMismatches(matchedKeys, obj, anotherObj) ++
            missingKeys.map(key => s"Missing key: $key") ++
            newKeys.map(key => s"Unexpected new key: $key")
        }
        else {
          if (anotherJson.isNull) {
            Set.empty[String]
          }
          else {
            Set(s"Cannot compare a json object to ${jsonType(anotherJson)}.")
          }
        }
      }
      else if (json.isArray) {
        if (anotherJson.isArray) {
          val aMismatchB = for {a <- json.asArray.get
                                b <- anotherJson.asArray.get
                                } yield a.schemaMismatches(b)

          val bMismatchA = for {
            b <- anotherJson.asArray.get
            a <- json.asArray.get
          } yield b.schemaMismatches(a)
          aMismatchB.flatten.toSet ++ bMismatchA.flatten.toSet
        }
        else {
          if (anotherJson.isNull) {
            Set.empty[String]
          }
          else {
            Set(s"Cannot compare a json arrat to ${jsonType(anotherJson)}.")
          }
        }
      }
      else {
        Set(s"Cannot compare ${jsonType(json)} to ${jsonType(anotherJson)}.")
      }
    }
  }

}