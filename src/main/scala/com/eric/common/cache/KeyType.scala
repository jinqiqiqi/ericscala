package com.eric.common.cache

import akka.pattern.ask
import akka.util.Timeout
import com.eric.common.{CacheActorWrapper, PinCache}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by kinch on 12/28/16.
  */

trait KeyType extends BaseType {
  def key: String
  def allKeys: Seq[String] = Seq(key)
}

object KeyType {
  def pin(ks: KeyType*)(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout): Future[Boolean] = {
    val fs = ks.groupBy(_.db) map {

      case (db, keys) => {
        cm.actor ? PinCache(db, keys.flatMap(_.allKeys), force = false)
      }

    }


    Future.sequence(fs).map(_ => true)
  }
}
