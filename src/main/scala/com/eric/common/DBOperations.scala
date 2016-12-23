package com.eric.common

import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ ExecutionContext, Future }





case class DBOperations(implicit ec: ExecutionContext, to: Timeout, db: DBActorWrapper) {

  def load(tbl: String, eids: Seq[Long], attrs: Seq[AttrSpec]): Future[Response] = {
    val fs = eids.grouped(db.batchSize).map { chunk =>
      (db.actor ? LoadEntities(tbl, chunk, attrs)).mapTo[Response]
    }

    Future.sequence(fs).map(res => errorOrElse[ValueLists](res.toSeq)(lists => ValueLists(lists.flatMap(_.vss))))
  }

  def errorOrElse[T](list: Seq[Response])(fn: Seq[T] => Response): Response =
    list.find(_.isInstanceOf[Failed]).getOrElse(fn(list.map(_.asInstanceOf[T])))

  def select(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int, range: Int) =
    (db.actor ? Query(sql, cols, binds, start, range)).mapTo[Response]

}

