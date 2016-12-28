package com.eric.common.cache

import akka.pattern.ask
import akka.util.Timeout
import com.eric.common._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by kinch on 12/28/16.
  */
trait BaseType {
  val db = 0

  def get(k: String)(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout): Future[String] =
    (cm.actor ? CacheGet(k, db)).map{
      case CachedValue(v) => v
    }

  def mget(ks: Seq[String])(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout): Future[Seq[String]] =
    (cm.actor ? CacheMGet(ks, db)).map {
      case CachedValues(vs) if vs.isEmpty => Seq.fill(ks.size)("")
      case CachedValues(vs) => vs
    }

  def set(k: String, v: String)(implicit cm: CacheActorWrapper) = cm.actor ! CacheSet(k, v, db)

  def mset(kvs: Seq[(String, String)])(implicit cm: CacheActorWrapper) = cm.actor ! CacheMSet(kvs, db)

  def incr(k: String, by: Int)(implicit cm: CacheActorWrapper): Unit = cm.actor ! CacheIncr(k, by, db)

  def remove(ks: Seq[String])(implicit cm: CacheActorWrapper) = cm.actor ! CacheRemove(ks, db)

  def exists(k: String)(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout): Future[Boolean] =
    (cm.actor ? CacheExists(k, db)).map {
      case CachedBoolean(x) => x
    }
}
