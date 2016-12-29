package com.eric.impl

import akka.actor.Actor
import akka.pattern.pipe
import akka.util.Timeout
import com.eric.common.Constants.Attr
import com.eric.{CacheActor, DatabaseActor}
import com.eric.common._
import com.eric.common.cache.EntityCache
import com.eric.common.ReturnCode._

import scala.concurrent.Future

case class EntityManager(batchSize: Int)(implicit to: Timeout) extends Actor with DateUtil {
  import context.dispatcher

  implicit val cm = CacheActorWrapper(context.system.actorSelection(CacheActor.actorPath))
  implicit val dbactor = DBActorWrapper(context.system.actorSelection(DatabaseActor.actorPath), batchSize)

  val database = DBOperations()
  val ecache = EntityCache(batchSize)

  private def load1(tn: String, eid: Long): Future[Response] = get(tn) { t =>
    ecache.load1(t.tn, eid) (miss => database.load1(t.dbTable, miss, t.et.attrs))
  }

  private def load(tn: String, p: String): Future[Response] = get(tn) { t =>
    ecache.load(t.tn, p.split(",").map(_.toLong).toSeq)(misses => database.load(t.dbTable, misses, t.et.attrs))
  }

  private def append(t: Types, attrname: String, vs: Map[String, String]): Map[String, String] =
    if(t.attrnames.contains(attrname)) vs + (attrname -> now.toString) else vs

  private def createTime(t: Types, vs: Map[String, String]) =
    append(t, Attr.CREATED, append(t, Attr.MODIFIED, vs))

  private def create(tn: String, kvs: Map[String, String]): Future[Response] = get(tn) { t =>
    t.binds(createTime(t, kvs)) match {
      case Left(err) => Future.successful(err)
      case Right(vars) => database.insert(t.dbTable, vars)
    }
  }

  private def modifyTime(t: Types, vs: Map[String, String]) =
    append(t, Attr.MODIFIED, vs - Attr.CREATED)

  private def update(tn: String, ks: Map[String, String], vs: Map[String, String]): Future[Response] = get(tn) { t =>
    (t.binds(ks), t.binds(modifyTime(t, vs))) match {
      case (Left(err), _) => Future.successful(err)
      case (_, Left(err)) => Future.successful(err)
      case (Right(keys), Right(values)) => database.update(t.dbTable, keys, values) { res =>
        def convertDates(vs: Map[String, String]): Map[String, String] =
          vs.map {
            case (k, v) => (k, if(t.isDate(k)) seconds2str(v.toLong) else v)
          }
        ks.collectFirst {
          case (k, v) if k == t.primary => ecache.update(t.tn, v.toLong, convertDates(vs))
        }
        Future.successful(res)
      }
    }
  }

  private def delete(tn: String, ks: Map[String, String]): Future[Response] = get(tn) { t =>
    t.binds(ks) match {
      case Left(err) => Future.successful(err)
      case Right(binds) => database.delete(t.dbTable, binds) { res =>
        // Remove entity cache if the primary key is given
        ks.collectFirst {
          case (k, v) if k == t.primary => ecache.evict(t.tn, v.toLong) 
        }
        Future.successful(res)
      }
    }
  }

  private def mcreate(tn: String, kvs: Seq[Map[String, String]]) = get(tn){t =>
    val binds = kvs.map { vs => t.binds(createTime(t, vs)) }
    binds.find(_.isLeft) match {
      case Some(err) => Future.successful(err.left.get)
      case _ => database.minsert(t.dbTable, binds.map(_.right.get))
    }
  }

  private def deleteNCreate(tn: String, ks: Map[String, String], kvs: Seq[Map[String, String]]) =
    delete(tn, ks).flatMap {
      case err: Failed => Future.successful(err)
      case _ => mcreate(tn, kvs)
    }

  private def getType(tn: String) = get(tn)(t => Future.successful(t.et))

  private def get(tn: String)(fn: Types => Future[Response]): Future[Response] = Types.get(tn) match {
    case Some(t) => fn(t)
    case None => Future.successful(report(TypeNotFound, tn))
  }

  def receive = {
    case GetEntity(tn, eid) => load1(tn, eid).pipeTo(sender())
    case GetEntities(tn, eids) => load(tn, eids).pipeTo(sender())
    case CreateEntity(tn, kvs) => create(tn, kvs).pipeTo(sender())
    case UpdateEntity(tn, ks, vs) => update(tn, ks, vs).pipeTo(sender())
    case DeleteEntities(tn, eids) => delete(tn, eids).pipeTo(sender())
    case CreateEntities(tn, kvs) => mcreate(tn, kvs).pipeTo(sender())
    case DeleteAndCreate(tn, ks, kvs) => deleteNCreate(tn, ks, kvs).pipeTo(sender())
    case GetEntityType(tn) => getType(tn).pipeTo(sender())
  }

}
