package com.liyutech.common

import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}
import java.util.Date

object TimeUtil {
  def toLocalDate(date: Date): LocalDate = {
    toLocalDateTime(date).toLocalDate()
  }

  def toLocalDateTime(date: Date): LocalDateTime = {
    val instant = Instant.ofEpochMilli(date.getTime)
    LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
  }

  def toLocalDateTime(date: LocalDate): LocalDateTime = {
    date.atStartOfDay()
  }
}
