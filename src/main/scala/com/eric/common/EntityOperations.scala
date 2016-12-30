package com.eric.common

import com.eric.common.Constants.Gender
import scala.concurrent.{ExecutionContext, Future}

import akka.pattern.ask
import akka.util.Timeout



abstract class EntityOperations(implicit ec: ExecutionContext, to: Timeout, em: EntityActorWrapper, cm: CacheActorWrapper) extends DateUtil {
  def t: Types

  def validate(vs: Map[String, String], keys: String*)(fn: => Future[Response]): Future[Response] =
    keys.filterNot(k => t.attrnames.contains(k)) match {
      case xs if xs.nonEmpty => Future.successful(ReturnCode.report(ReturnCode.InvalidAttributes, xs.mkString(",")))
      case _ => keys.filter(k => vs.get(k.toString()).isEmpty) match {
        case xs if xs.nonEmpty => Future.successful(ReturnCode.report(ReturnCode.MissingParameters, xs.mkString(",")))
        case _ => fn
      }
    }

  
  def extract(vs: Map[String, String], ans: String*)(fn: Product => Future[Response]): Future[Response] = 
    validate(vs, ans: _*)(ans.toList.map(n => (vs(n), t.attrs.find(_._1 == n).get._3)).map {
      case (v, Datatype.LONG_TYPE) => v.toLong
      case (v, Datatype.STRING_TYPE) => v
      case (v, Datatype.INT_TYPE) => v.toInt
      case (v, Datatype.DATE_TYPE) => v
      case (v, Datatype.BOOLEAN_TYPE) => v == "1"
      case (v, Datatype.DOUBLE_TYPE) => v.toDouble
      case (v, Datatype.GENDER_TYPE) => v.toInt
    } match {
      case List(a) => fn(Tuple1(a))
      case List(a, b) => fn(a, b)
      case List(a, b, c) => fn(a, b, c)
      case List(a, b, c, d) => fn(a, b, c, d)
      case List(a, b, c, d, e) => fn(a, b, c, d, e)
      case List(a, b, c, d, e, f, g) => fn(a, b, c, d, e, f, g)
    })

  def getOrElse(vs: Map[String, String], ans: String*)(fn: Product => Future[Response]): Future[Response] = {
    ans.toList.partition(n => t.attrMap.isDefinedAt(n)) match {
      case (_, invalid) if invalid.nonEmpty =>
        Future.successful(ReturnCode.report(ReturnCode.InvalidAttributes, invalid.mkString(",")))
      case (valid, _) =>
        valid.map(n => (vs.get(n), t.attrMap(n)._3)).map {
          case (v, Datatype.LONG_TYPE) => if (v.isDefined) v.get.toLong else 0L
          case (v, Datatype.INT_TYPE) => if (v.isDefined) v.get.toInt else 0
          case (v, Datatype.DATE_TYPE) => if (v.isDefined) v.get else defaultDatTime
          case (v, Datatype.BOOLEAN_TYPE) => if (v.isDefined) v.get == "1" else false
          case (v, Datatype.DOUBLE_TYPE) => if (v.isDefined) v.get.toDouble else 0.0
          case (v, Datatype.GENDER_TYPE) => if (v.isDefined) v.get.toInt else Gender.UNKNOWN
          case (v, _) => if (v.isDefined) v.get else ""
        } match {
          case List(a) => fn(Tuple1(a))
          case List(a, b) => fn(a, b)
          case List(a, b, c) => fn(a, b, c)
          case List(a, b, c, d) => fn(a, b, c, d)
          case List(a, b, c, d, e) => fn(a, b, c, d, e)
          case List(a, b, c, d, e, f) => fn(a, b, c, d, e, f)
          case List(a, b, c, d, e, f, g) => fn(a, b, c, d, e, f, g)
          case List(a, b, c, d, e, f, g, h) => fn(a, b, c, d, e, f, g, h)
          case List(a, b, c, d, e, f, g, h, i) => fn(a, b, c, d, e, f, g, h, i)

        }
    }
  }

  def getAttrs(eid: Long, ans: String*)(fn: Product => Future[Response]): Future[Response] =
    get(eid)(en => getOrElse(en.kvs, ans: _*)(res => fn(res)))

  def get(eid: Long)(fn: Entity => Future[Response]): Future[Response] =
    (em.actor ? GetEntity(t.tn, eid)) flatMap {
      case err: Failed => Future.successful(err)
      case en: Entity => fn(en)
    }
  
}
