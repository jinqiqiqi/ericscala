package com.eric.impl

import com.eric.common.CacheActorWrapper

import scala.concurrent.ExecutionContext

/**
 * Created by kinch on 12/21/16.
 */
trait Util {
  def init()(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: akka.util.Timeout) = {

  }

}
