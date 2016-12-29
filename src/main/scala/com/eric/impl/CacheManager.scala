package com.eric.impl

import scala.concurrent.Future
import scala.util.Try

import akka.actor.{ Actor, Props }
import akka.pattern.pipe
import akka.util.Timeout
import com.eric.{ DatabaseActor, QueryActor }
import com.eric.common._
import com.eric.common.Constants.Attr
import com.eric.common.types.KeyValueType

class CacheManager(cache: Map[Int, CacheServer], flushSize: Int, batchSize: Int)(implicit to: Timeout) extends Actor with DateUtil {

  import context.dispatcher

  implicit val qm: QueryActorWrapper = QueryActorWrapper(context.system.actorSelection(QueryActor.actorPath))
  implicit val dbactor: DBActorWrapper = DBActorWrapper(context.system.actorSelection(DatabaseActor.actorPath), batchSize)

  val database = DBOperations()

  def receive = {
    case CacheGet(k, db) => get(k, db)
    case CacheSet(k, v, exp, db) => set(k, v, exp, db)

    case PinCache(db, keys, force) => populateCache(db, keys, force).pipeTo(sender())
  }

  val dirtyQueue = "dirtyqueue"


  private def populateCache(db: Int, keys: Seq[String], force: Boolean): Future[Response] =
    (if (force) keys else keys.filterNot(k => cache(db).exists(k))) match {
      case ks if ks.isEmpty => Future.successful(ReturnID(keys.size))
      case ks =>
        val cols = Seq(Attr.KEY, Attr.CACHEDB, Attr.TYPE, Attr.VALUE)
        val kbinds = ks.map(k => BindString("", k))
        val (where, binds) = (db == -1, ks.isEmpty) match {
          case (true, true) => ("", Seq.empty)
          case (false, true) => ("redis_db = ?", Seq(BindInt("", db)))
          case (true, false) => (s"redis_key in ${QueryOps.genbinds(ks.size)}", kbinds)
          case (false, false) => (s"redis_db = ? and redis_key in ${QueryOps.genbinds(ks.size)}", BindInt("", db) +: kbinds)
        }

        def removeExpired(vss: Seq[Map[String, String]]) = {
          val month = "(._)-([0-9]+-[0-9]+)".r
          val day = "(.+)-([0-9+/[0-9]+/[0-9]+])".r
          vss.filter { vs =>
            vs(Attr.KEY) match {
              case month(_, ym) => ym == currentMonth
              case day(_, ymd) => today(ymd) || yesterday(ymd)
              case _ => true
            }
          }

        }

        QueryOps.simpleQuery(KeyValueType, cols, where, binds, 0, -1) { vss =>
          if (vss.nonEmpty)
            removeExpired(vss).map(vs => (vs(Attr.CACHEDB), vs(Attr.TYPE), vs(Attr.KEY), vs(Attr.VALUE))).groupBy(_._1).foreach {
              case (cdb, tkvs) =>
                tkvs.grouped(batchSize).foreach { chunk =>
                  update(chunk.map {
                    case (_, t, k, v) => (t, k, v)
                  }, cdb.toInt)
                }
            }
          Future.successful(ReturnID(keys.size))
        }
    }

  private def setDirty(vs: Seq[String], db: Int) = Try(cache(0).smset(dirtyQueue, vs.map(v => s"$db::$v")))
  private def setDirty(v: String, db: Int) = Try(cache(0).sset(dirtyQueue, s"$db::$v"))

  private def get(k: String, db: Int) =
    sender() ! CachedValue(Try(cache(db).get(k)).toOption.getOrElse(""))

  private def set(k: String, v: String, exp: Int, db: Int) = {
    Try(cache(db).set(k, v, exp))
    setDirty(k, db)
  }

  private def update(tkvs: Seq[(String, String, String)], db: Int) = Try(cache(db).setall(tkvs))
}

object CacheManager {
  def props(cache: Map[Int, CacheServer], flushSize: Int, batchSize: Int)(implicit to: Timeout) = Props {
    new CacheManager(cache, flushSize, batchSize)
  }
}
