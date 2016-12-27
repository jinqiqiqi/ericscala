package com.eric.common

import com.redis.{RedisClient, RedisClientPool}


trait CacheServer {
  def exists(k: String): Boolean
  def get(k: String): String
  def mget(ks: Seq[String]): Seq[String]
  def set(k: String, v: String, exp: Int): Boolean

  def sset(k: String, v: String): Boolean
  def smset(k: String, vs: Seq[String]): Option[Long]
}


case class RedisCache(rp: RedisClientPool) extends CacheServer {
  def expire[T](k: String, exp: Int)(fn: RedisClient => T): T = rp.withClient { r =>
    if(exp == 0 || r.exists(k))
      fn(r)
    else {
      val res = fn(r)
      r.expire(k, exp)
      res
    }
  }

  def exists(k: String): Boolean = rp.withClient { r => r.exists(k) }

  def get(k: String): String = rp.withClient { r => r.get(k).getOrElse("")}

  def mget(ks: Seq[String]): Seq[String] = rp.withClient { r => r.mget(ks.head, ks.tail: _*).getOrElse(Seq.empty).map(_.getOrElse("")) }

  def set(k: String, v: String, exp: Int): Boolean = exp match {
    case 0 => rp.withClient { r => r.set(k, v) }
    case _ => rp.withClient { r => r.setex(k, exp.toLong, v) }
  }

  def sset(k: String, v: String): Boolean = rp.withClient(r => r.sadd(k, v).getOrElse(0L) == 1)

  def smset(k: String, vs: Seq[String]) = rp.withClient(r => r.sadd(k, vs.head, vs.tail: _*))

  def incr(k: String, by: Int):Long = rp.withClient { r => r.incrby(k, by).getOrElse(0) }


}
