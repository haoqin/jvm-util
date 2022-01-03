package com.liyutech.quill.model

case class FlywaySchemaHistory(installedRank: Int, version: Option[String], description: String, `type`: String, script: String, checksum: Option[Int], installedBy: String, installedOn: java.time.LocalDateTime, executionTime: Int, success: Boolean)
