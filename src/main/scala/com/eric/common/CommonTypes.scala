package com.eric.common

import akka.actor.ActorSelection

import scala.util.Try

/**
  * Created by kinch on 12/21/16.
  */

sealed trait BindValue {
  def colname: String
}

case class BindLong(colname: String, v: Long) extends BindValue
case class BindInt(colname: String, v: Int) extends BindValue
case class BindString(colname: String, v: String) extends BindValue
case class BindBoolean(colname: String, v: Boolean) extends BindValue
case class BindDouble(colname: String, v: Double) extends BindValue
case class BindDate(colname: String, v: Long) extends BindValue
case class BindGender(colname: String, v: Int) extends BindValue

object BindValue {
  def bind(colname: String, dt: Int, v: String) = dt match {
    case Datatype.LONG_TYPE => BindLong(colname, v.toLong)
    case Datatype.INT_TYPE => BindInt(colname, v.toInt)
    case Datatype.STRING_TYPE => BindString(colname, v)
    case Datatype.BOOLEAN_TYPE => BindBoolean(colname, v == "1")
    case Datatype.DOUBLE_TYPE => BindDouble(colname, v.toDouble)
    case Datatype.DATE_TYPE => BindLong(colname, v.toLong)
    case Datatype.GENDER_TYPE => BindGender(colname, v.toInt)
    case _ => BindString(colname, v)
  }
}

object Datatype {
  val LONG_TYPE = 1
  val INT_TYPE = 2
  val STRING_TYPE = 3
  val BOOLEAN_TYPE = 4
  val DOUBLE_TYPE = 5
  val DATE_TYPE = 6
  val GENDER_TYPE = 7
}

case class EntityActorWrapper(actor: ActorSelection)
case class QueryActorWrapper(actor: ActorSelection)
case class CacheActorWrapper(actor: ActorSelection)
case class DBActorWrapper(actor: ActorSelection, batchSize: Int)


case class AttrSpec(attrname: String, colname: String, dt: Int)

// case classes for models, an entity type
case class EntityType(tn: String, dbTable: String, attrs: Seq[AttrSpec]) extends Response


case class Entity(tn: String, eid: Long, kvs: Map[String, String]) extends Response {
  def getAttr(n: String): Option[String] = kvs.get(n)
  def getString(n: String): String = kvs.getOrElse(n, "")
  def getLong(n: String): Long = kvs.get(n) match {
    case Some(v) => Try(v.toLong).toOption.getOrElse(0L)
    case _ => 0L
  }
  def getTimestamp(n: String) = getLong(n)
  def project(ans: Seq[String]) = kvs.filter(kv => ans.contains(kv._1))
}

object Entity {
  def apply(tn: String, vs: Map[String, String]): Entity = new Entity(tn, vs(Constants.Attr.ID).toLong, vs)
}