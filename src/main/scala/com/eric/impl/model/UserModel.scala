package com.eric.impl.model

import akka.util.Timeout
import com.eric.common._

import scala.concurrent.ExecutionContext

/**
  * Created by kinch on 12/21/16.
  */
case class UserModel(implicit qm: QueryActorWrapper, cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout) {
  def profile(uid: Long) = {
    QueryOps.query()
  }
}
