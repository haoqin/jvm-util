package com.liyutech.quill.model

case class PodAutokey(id: Int, podUsername: String, podName: Option[String], podDescription: Option[String], emoji: Option[String], isPublic: Option[Boolean], isDm: Option[Boolean], updatedAt: java.time.LocalDateTime)