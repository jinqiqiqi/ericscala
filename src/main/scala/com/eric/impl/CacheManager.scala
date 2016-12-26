package com.eric.impl

import akka.actor.{ Actor, Props }
import akka.util.Timeout
import com.eric.common.{ DateUtil, CacheServer }



class CacheManager(cache: Map[Int, CacheServer], flushSize: Int, batchSize: Int) extends Actor with DateUtil {
  def receive = {
    case "" => ???
  }
}

object CacheManager {
  def props(cache: Map[Int, CacheServer], flushSize: Int, batchSize: Int)(implicit to: Timeout) = Props { new CacheManager(cache, flushSize, batchSize)}
}
