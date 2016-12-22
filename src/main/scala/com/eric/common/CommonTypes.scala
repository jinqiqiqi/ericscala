package com.eric.common

import akka.actor.ActorSelection

/**
  * Created by kinch on 12/21/16.
  */

sealed trait BindValue {
  def colname: String
}

case class BindLong(colname: String, v: Long) extends BindValue
case class BindInt(colname: String, v: Int) extends BindValue
case class BIndString(colname: String, v: String) extends BindValue
case class BindBoolean(colname: String, v: Boolean) extends BindValue
case class BindDouble(colname: String, v: Double) extends BindValue
case class BindDate(colname: String, v: Long) extends BindValue
case class BindGender(colname: String, v: Int) extends BindValue

object BindValue {
  def bind(colname: String, dt: Int, v: String) = dt match {
    case Datatype.LONG_TYPE => BindLong(colname, v.toLong)
    case Datatype.INT_TYPE => BindInt(colname, v.toInt)
    case Datatype.STRING_TYPE => BIndString(colname, v)
    case Datatype.BOOLEAN_TYPE => BindBoolean(colname, v == "1")
    case Datatype.DOUBLE_TYPE => BindDouble(colname, v.toDouble)
    case Datatype.DATE_TYPE => BindLong(colname, v.toLong)
    case Datatype.GENDER_TYPE => BindGender(colname, v.toInt)
    case _ => BIndString(colname, v)
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

class CommonTypes {
}

case class QueryActorWrapper(actor: ActorSelection)
case class CacheActorWrapper(actor: ActorSelection)
case class DBActorWrapper(actor: ActorSelection, batchSize: Int)

case class AttrSpec(attrname: String, colname: String, dt: Int)

// case classes for models, an entity type
case class EntityType(tn: String, dbTable: String, attrs: Seq[AttrSpec]) extends Response