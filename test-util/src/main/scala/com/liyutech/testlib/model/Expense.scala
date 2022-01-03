package com.liyutech.testlib.model

case class Expense(id: String, senderUserId: String, podUserId: String, message: Option[String], amount: Double, updatedAt: java.time.LocalDateTime)
