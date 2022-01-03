package com.liyutech.testlib.model

case class Pod(id: String, podUsername: String, podName: Option[String], podDescription: Option[String], emoji: Option[String], isPublic: Option[Boolean], isDm: Option[Boolean], updatedAt: java.time.LocalDateTime)
