package com.liyutech.quill.model

case class OrcaTransaction(id: String, podUserId: String, senderUserId: String, receiverUserId: String, expenseId: String, message: Option[String], amount: Double, bookingId: Option[String], isPending: Option[Boolean], updatedAt: java.time.LocalDateTime)
