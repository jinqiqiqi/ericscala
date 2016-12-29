package com.eric.common

import com.eric.common.ReturnCode._
import com.eric.common.types._

/**
  * Created by kinch on 12/22/16.
  */
trait Types {
  def tn: String

  def dbTable: String

  def primary: String = Constants.Attr.ID

  def attrs: Seq[(String, String, Int)]
  def attrMap: Map[String, (String, String, Int)] = attrs.map(x => (x._1, x)).toMap
  def attrnames: Seq[String] = attrs.map(_._1)
  def colsnames: Seq[String] = attrs.map(_._2)

  def et: EntityType = EntityType(tn, dbTable, attrs.map {
    case (an, cn, dt) => AttrSpec(an, cn, dt)
  })

  def isDate(an: String): Boolean = attrMap.get(an) match {
    case Some(x) => x._3 == Datatype.DATE_TYPE
    case None => false
  }

  // Given a map of key/value pairs, generate an array of bind variables
  def binds(kvs: Map[String, String]): Either[Failed, Seq[BindValue]] = {
    val input = kvs.toSeq.map(kv => (kv._1.toLowerCase(), kv._2))
    input.find(kv => attrMap.get(kv._1).isEmpty) match {
      case Some(v) => Left(ReturnCode.report(InvalidBindVariables, v._1))
      case None =>
        Right(input.map {case (k, v) =>
          attrMap(k) match {
            case (_, colname, dt) => BindValue.bind(colname, dt, v)
          }
        })
    }
  }

  def project(ans: Seq[String], kvs: Map[String, String]): Seq[(String, Int, String)] =
    ans.map(_.toLowerCase()).map(an => (an, attrs.find(_._1 == an))).flatMap {
      case (_, None) => None
      case (n, Some(attr)) => Some((n, attr._3, kvs.getOrElse(n, "")))
    }
}

object Types {
  val allTypes = Seq(UserType, KeyValueType)
  val mp: Map[String, Types] = allTypes.map(t => (t.tn, t)).toMap
  def get(tn: String): Option[Types] = mp.get(tn.toLowerCase())
  def getType(tn: String): Types = get(tn).get
}
