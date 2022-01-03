package com.liyutech.testlib.model

case class Friend(id: String, requesterUserId: String, requestedUserId: String, isBlocked: Option[Boolean], updatedAt: java.time.LocalDateTime)
