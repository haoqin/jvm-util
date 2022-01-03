package com.liyutech.quill.model

case class NotificationToken(id: String, userId: String, deviceId: Option[String], token: String, isActive: Option[Boolean], updatedAt: java.time.LocalDateTime)
