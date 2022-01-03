package com.liyutech.quill.model

case class Friend(id: String, requesterUserId: String, requestedUserId: String, isBlocked: Option[Boolean], updatedAt: java.time.LocalDateTime)
