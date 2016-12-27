package com.eric.impl

import scala.util.Try

import akka.actor.{Actor, Props}
//import akka.pattern.pipe
import akka.util.Timeout
import com.eric.{DatabaseActor, QueryActor}
import com.eric.common._



class CacheManager(cache: Map[Int, CacheServer], flushSize: Int, batchSize: Int)(implicit to: Timeout) extends Actor with DateUtil {


  import context.dispatcher

  implicit val qm = QueryActorWrapper(context.system.actorSelection(QueryActor.actorPath))
  implicit val dbactor = DBActorWrapper(context.system.actorSelection(DatabaseActor.actorPath), batchSize)

  val database = DBOperations()


  def receive = {
    case CacheGet(k, db) => get(k, db)
    case CacheSet(k, v, exp, db) => set(k, v, exp, db)

  }

  val dirtyQueue = "dirtyqueue"

  private def setDirty(vs: Seq[String], db: Int) = Try(cache(0).smset(dirtyQueue, vs.map(v => s"$db::$v")))
  private def setDirty(v: String, db: Int) = Try(cache(0).sset(dirtyQueue, s"$db::$v"))

  private def get(k: String, db: Int) =
    sender() ! CachedValue(Try(cache(db).get(k)).toOption.getOrElse(""))

  private def set(k: String, v: String, exp: Int, db: Int) = {
    Try(cache(db).set(k, v, exp))
    setDirty(k, db)
  }
}

object CacheManager {
  def props(cache: Map[Int, CacheServer], flushSize: Int, batchSize: Int)(implicit to: Timeout) = Props {
    new CacheManager(cache, flushSize, batchSize)
  }
}
