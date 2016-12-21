package com.eric.common

import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
  * Created by kinch on 12/21/16.
  */
trait DateUtil {

  val defaultDatTime = "1970/01/01 00:00:00"
  val zoneOffset = ZoneOffset.of("+08:00")
  val sFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

  def seconds2str(seconds: Long) = {
    if (seconds == 0)
      defaultDatTime
    else
      java.time.LocalDateTime.ofEpochSecond(seconds, 0, zoneOffset).format(sFormatter)
  }
}
