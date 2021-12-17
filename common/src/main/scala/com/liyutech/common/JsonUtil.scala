package com.liyutech.common

import io.circe.Json

object JsonUtil {

  implicit class Tuple2SeqDecorator[A, B](input: Seq[(A, B)]) {
    def toJson(fn: (A, B) => Json): Json = tuple2ToJson(input)(fn)
  }

  def tuple2ToJson[A, B](input: Seq[(A, B)])(fn: (A, B) => Json): Json = {
    Json.fromValues(input.map { case (a, b) => fn(a, b) })
  }
}