package com.liyutech.testlib.model

case class Notification(id: String, senderUserId: String, receiverUserId: String, message: Option[String], navParams: Option[String], updatedAt: java.time.LocalDateTime)
