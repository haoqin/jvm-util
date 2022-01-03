package com.liyutech.testlib.model

case class UserPodLookup(id: String, userId: String, podId: String, isActive: Option[Boolean], permission: Option[Int], updatedAt: java.time.LocalDateTime)
