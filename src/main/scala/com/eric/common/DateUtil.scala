package com.eric.common

import java.time.{ LocalDateTime, ZoneId, ZoneOffset }
import java.time.format.DateTimeFormatter
import java.time.LocalDate

/**
  * Created by kinch on 12/21/16.
  */
trait DateUtil {

  // format settings
  val defaultDatTime = "1970/01/01 00:00:00"
  val zoneOffset = ZoneOffset.of("+08:00")
  val dFormatter = DateTimeFormatter.ofPattern("yyy/MM/dd")
  val mFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
  val sFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

  val currentZoneId = ZoneId.of("Asia/Shanghai")


  // 
  def currentDate: String = LocalDate.now(currentZoneId).format(dFormatter)
  def currentMonth: String = LocalDate.now(currentZoneId).format(mFormatter)

  def previousDate: String = LocalDate.now(currentZoneId).minusDays(1).format(dFormatter)
  def previousMonth: String = LocalDate.now(currentZoneId).minusMonths(1).format(mFormatter)

  def today(str: String): Boolean = str.nonEmpty && LocalDate.parse(str, sFormatter).format(dFormatter) == currentDate
  def yesterday(str: String): Boolean = str.nonEmpty && LocalDate.parse(str, sFormatter).format(dFormatter) == previousDate

  def seconds2str(seconds: Long) = {
    if (seconds == 0)
      defaultDatTime
    else
      LocalDateTime.ofEpochSecond(seconds, 0, zoneOffset).format(sFormatter)
  }
}
