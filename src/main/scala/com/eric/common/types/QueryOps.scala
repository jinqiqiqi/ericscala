package com.eric.commom

import akka.pattern.ask
import akka.util.Timeout
import com.eric.common._
import scala.concurrent.{ ExecutionContext, Future }


case object QueryOps {

  def query(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int = 0, range: Int = -1)
           (fn: Seq[Map[String, String]] => Future[Response])
           (implicit ec: ExecutionContext, to: Timeout, qm: QueryActorWrapper): Future[Response] = {
    (qm.actor ? Query(sql, cols, binds, start, range)).flatMap {
      case err: Failed => Future.successful(err)
      case ValueList(vss) => fn(vss)
    }
  }

  def simpleQuery(t: Types, ans: Seq[String], where: String, binds: Seq[BindValue], start: Int = 0, range: Int = -1)
                 (fn: Seq[Map[String, String]] => Future[Response])
                 (implicit ec: ExecutionContext, to: Timeout, qm: QueryActorWrapper): Future[Response] = {
    val (attrs, cols) = ans.flatMap { n =>
      t.attrs.find(_._1 == n) match {
        case Some(x) => Some((x._1, x._3), x._2)
        case None => None
      }
    }.unzip

    val wclause = if (where.isEmpty) "" else s" where $where"
    query(s"select ${cols.mkString(",")} from ${t.dbTable} $wclause", attrs, binds, start, range)(fn)
  }
}
