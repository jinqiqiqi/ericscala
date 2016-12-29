package com.eric.common.cache

import akka.util.Timeout
import com.eric.common._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by kinch on 12/29/16.
  */
case class EntityCache(batchSize: Int)(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout) extends JsonType{

  def load(tn: String, eids: Seq[Long])(accessDB: Seq[Long] => Future[Response]): Future[Response] = {
    val fs = eids.grouped(batchSize).map ( chunk => mget(tn, eids))
    Future.sequence(fs).flatMap { results =>
      val inCache = results.flatten.flatten.toSeq
      val misses = eids.diff(inCache.map(_.eid))

      if(misses.isEmpty)
        Future.successful(Entities(inCache))
      else
        accessDB(misses).map {
          case err: Failed => err
          case ValueList(kvss) =>
            // Entities(inCache ++ store(tn, kvss.map( kvs => (kvs(Types.getType(tn).primary).toLong, removeEmpty(kvs)))))
          Entities(inCache ++ store(tn, kvss.map(kvs => (kvs(Types.getType(tn).primary ).toLong, removeEmpty(kvs) )  )) )
        }
    }

  }

  def store(tn: String, kvs: Seq[(Long, Map[String, String])]) = {
    kvs.grouped(batchSize) foreach ( chunk => mset(tn, chunk))
    kvs.map { case (eid, attrs) => Entity(tn, eid, attrs) }
  }

  def store1(tn: String, eid: Long, kvs: Map[String, String]) = {
    set(tn, eid, kvs)
    Entity(tn, eid, kvs)
  }

  def removeEmpty(kvs: Map[String, String]): Map[String, String] = kvs.filter(_._2.nonEmpty)

  def load1(tn: String, eid: Long)(accessDB: Long => Future[Response]): Future[Response] =
    get(tn, eid).flatMap {
      case Some(en) => Future.successful(en)
      case None => accessDB(eid).map {
        case err: Failed => err
        case ValueList(kvs) => store1(tn, eid, removeEmpty(kvs))
      }
    }

  def update(tn: String, eid: Long, nvs: Map[String, String]) =
    get(tn, eid).map {
      case Some(en) => set(tn, eid, removeEmpty(en.kvs ++ nvs))
      case _ =>
    }
}
