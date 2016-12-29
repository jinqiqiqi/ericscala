package com.eric.common.cache

import akka.util.Timeout
import spray.json._
import com.eric.common.{CacheActorWrapper, Entity}

import scala.concurrent.{ExecutionContext, Future}

import com.eric.common._
import Serialize.entitybody

/**
  * Created by kinch on 12/29/16.
  */
trait JsonType extends BaseType {
  def toEntity(v: String) = if(v.isEmpty) None else Some(v.parseJson.convertTo[Entity])

  def fromEntity(en: Entity): String = en.toJson.toString()

  def key(tn: String, eid: Long) = s"$tn-$eid"

  def get(tn: String, eid: Long)(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout): Future[Option[Entity]] =
    super.get(key(tn, eid)).map(s => toEntity(s))

  def mget(tn: String, eids: Seq[Long])(implicit cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout): Future[Seq[Option[Entity]]] =
    super.mget(eids.map(eid => key(tn, eid))).map(vs => vs.map(v => toEntity(v)))

  def set(tn: String, eid: Long, attrs: Map[String, String])(implicit cm: CacheActorWrapper) =
    super.set(key(tn, eid), fromEntity((Entity(tn, eid, attrs))))

  def mset(tn: String, kvs: Seq[(Long, Map[String, String])])(implicit cm: CacheActorWrapper) =
    super.mset(kvs.map {
      case (eid, attrs) => (key(tn, eid), fromEntity(Entity(tn, eid, attrs)))
    })

  def remove(tn: String, eids: Seq[Long])(implicit cm: CacheActorWrapper) =
    super.remove(eids.map(eid => key(tn, eid)))
}
