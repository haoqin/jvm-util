package com.liyutech.common.model

import java.time.LocalDateTime

sealed trait UserModel
case class SimpleUserModel(uid: String, username: String) extends UserModel
case class SimpleLoginModel(uid: String, username: String, isRegistered: Option[Boolean], updatedAt: LocalDateTime) extends UserModel
case class AnonymousUserModel(uid: String) extends UserModel