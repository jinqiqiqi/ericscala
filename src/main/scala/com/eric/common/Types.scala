package com.eric.common

/**
  * Created by kinch on 12/22/16.
  */
trait Types {
  def tn: String

  def dbTable: String

  def primary: String = Constants.Attr.ID

  def attrs: Seq[(String, String, Int)]
  def attrMap = attrs.map(x => (x._1, x)).toMap
  def attrnames: Seq[String] = attrs.map(_._1)
  def colsnames: Seq[String] = attrs.map(_._2)

  def et = EntityType(tn, dbTable, attrs.map {
    case (an, cn, dt) => AttrSpec(an, cn, dt)
  })

  def isDate(an: String): Boolean = attrMap.get(an) match {
    case Some(x) => x._3 == Datatype.DATE_TYPE
    case None => false
  }
}
