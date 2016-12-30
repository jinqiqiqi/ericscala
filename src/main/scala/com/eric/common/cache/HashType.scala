package com.eric.common.cache

import akka.util.Timeout
import akka.pattern.ask
import com.eric.common.Constants.Attr
import com.eric.common._

import scala.concurrent.{ExecutionContext, Future}


sealed trait HashType[T] extends KeyType with DateUtil {
  override val db = 2
  val exp = 0

  def toValue(s: String): T

  def hset(f: T, v: Long)(implicit cm: CacheActorWrapper): Unit = hmset(Seq((f, v)))

  def hmset(fvs: Seq[(T, Long)])(implicit cm: CacheActorWrapper): Unit =
    if (fvs.nonEmpty) cm.actor ! CacheHMSet(key, fvs.map { case (f, v) => (f.toString(), v) }, db)

  def hget(f: T)(implicit ec: ExecutionContext, to: Timeout, cm: CacheActorWrapper): Future[Long] =
    hmget(Seq(f)).map(kv => kv.getOrElse(f, 0L))

  def hmget(vs: Seq[T])(implicit ec: ExecutionContext, to: Timeout, cm: CacheActorWrapper): Future[Map[T, Long]] =
    (cm.actor ? CacheHMGet(key, vs.map(_.toString()), db)).map {
      case CachedHValues(kv) => kv.map { case (f, v) => (toValue(f), v)}
    }

  def hincr(f: T, by: Int = 1)(implicit cm: CacheActorWrapper): Unit =
    cm.actor ! CacheHIncr(key, f.toString(), by, exp, db)

  def hdecr(f: T, by: Int = 1)(implicit cm: CacheActorWrapper): Unit = hincr(f, -by)

  def hdel(f: T)(implicit cm: CacheActorWrapper): Unit =
    cm.actor ! CacheHDel(key, f.toString(), db)

  def hgetall(implicit ec: ExecutionContext, to: Timeout, cm: CacheActorWrapper): Future[Map[T, Long]] =
    (cm.actor ? CacheHGetAll(key, db)) map {
      case CachedHValues(kv) => kv.map {
        case (f, v) => (toValue(f), v)
      }
    }

  def hexists(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout): Future[Boolean] = exists(key)

  def remove(implicit cm: CacheActorWrapper): Unit = super.remove(Seq(key))

}

sealed trait StringHashType extends HashType[String] {
  def toValue(s: String) = s
}

object StringHashType {
  def mhget(ids: Seq[Long], field: String)(key: Long => StringHashType)(implicit ec: ExecutionContext, to: Timeout, cm: CacheActorWrapper) =
    KeyType.pin(ids.map(id => key(id)): _*) flatMap { _ =>
      Future.sequence(ids.map(id => key(id).hget(field).map(v => (id, v)))).map (res => res.toMap)
    }

  def mhmget[T](ids: Seq[T], fields: Seq[String])(key: T => StringHashType)(implicit ec: ExecutionContext, to: Timeout, cm: CacheActorWrapper): Future[Map[T, Map[String, String]]] =
    KeyType.pin(ids.map(id => key(id)): _*).flatMap { _ =>
      Future.sequence(
        ids.map(id => key(id).hmget(fields).map(vs => (id, vs.map {
          case (k, v) => (k, v.toString())
        })))
      ). map { res =>
        val mp = res.toMap
        ids.map(id => (id, mp.getOrElse(id, Map.empty))).toMap
      }}
}

case class UserStats(uid: Long) extends StringHashType {
  val key = s"UserStats:$uid"
}
object UserStats {
  val fields = Seq(Attr.ID, Attr.USERNAME, Attr.PASSWORD, Attr.AGE, Attr.PROVINCE)
}


