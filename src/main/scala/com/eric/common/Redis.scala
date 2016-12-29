package com.eric.common

import com.redis.{RedisClient, RedisClientPool}


trait CacheServer {
  def exists(k: String): Boolean
  def get(k: String): String
  def mget(ks: Seq[String]): Seq[String]
  def set(k: String, v: String, exp: Int): Boolean

  def sset(k: String, v: String): Boolean
  def smset(k: String, vs: Seq[String]): Option[Long]

  def getall(ks: Seq[String]): Seq[(String, String)]
  def setall(tkvs: Seq[(String, String, String)]): Unit
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

  def smset(k: String, vs: Seq[String]): Option[Long] = rp.withClient(r => r.sadd(k, vs.head, vs.tail: _*))

  def incr(k: String, by: Int): Long = rp.withClient { r => r.incrby(k, by).getOrElse(0) }

  def hmset(k: String, vs: Seq[(String, Long)]): Boolean = rp.withClient { r =>
    r.hmset(k, vs)
  }
  def zmset(k: String, ks: Seq[(Double, String)]): Long = rp.withClient { r =>
    r.zadd(k, ks.head._1, ks.head._2, ks.tail: _*).getOrElse(0L)
  }

  def lpush(k: String, vs: Seq[String]): Long = rp.withClient { r =>
    r.lpush(k, vs.head, vs.tail: _*).getOrElse(0L)
  }

  def setall(tkvs: Seq[(String, String, String)]): Unit = rp.withClient{ r =>
    val pattern = "([^-]+)-(.+)".r
    tkvs foreach { case (t, k, vs) =>
      t match {
        case "hash" => hmset(k, vs.split(",").toSeq.filterNot(_.isEmpty()).map { case pattern(f, v) => (f, v.toLong) })
        case "zset" => zmset(k, vs.split(",").toSeq.filterNot(_.isEmpty()).map { case pattern(v, s) => (s.toDouble, v) })
        case "set" => smset(k, vs.split(",").toSeq.filterNot(_.isEmpty()))
        case "list" => lpush(k, vs.split(",").toSeq.filterNot(_.isEmpty()))
        case _ => set(k, vs, 0)
      }
    }
  }

  def getall(ks: Seq[String]) = rp.withClient { r =>
    ks.map { k =>
      r.getType(k) match {
        case None => ("", "")
        case Some(t) if t == "hash" => (t, r.hgetall1(k).getOrElse(Map.empty).map {case (f, v) => s"$f-$v"}.mkString(","))
        case Some(t) if t == "zset" => (t, r.zrangebyscoreWithScore(k, limit = Option((0, -1)), sortAs = RedisClient.DESC).getOrElse(List.empty).map { case (v, s) => s"$v-$s" }.mkString(","))
        case Some(t) if t == "set" => (t, r.smembers(k).getOrElse(Set.empty).flatten.mkString(","))
        case Some(t) if t == "list" => (t, r.lrange(k, 0, -1).getOrElse(List.empty).flatten.mkString(","))
        case Some(t) => (t, r.get(k).getOrElse(""))
      }
    }
  }


}
