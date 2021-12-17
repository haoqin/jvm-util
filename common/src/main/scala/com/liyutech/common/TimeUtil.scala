package com.liyutech.common

import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}
import java.util.Date

object TimeUtil {
  def toLocalDate(date: Date): LocalDate = {
    val instant = Instant.ofEpochMilli(date.getTime)
    LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
  }
}
