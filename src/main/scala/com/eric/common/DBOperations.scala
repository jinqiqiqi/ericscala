package com.eric.common

import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ ExecutionContext, Future }

case class DBOperations(implicit ec: ExecutionContext, to: Timeout, db: DBActorWrapper) {

  def insert(tbl: String, vs: Seq[BindValue]): Future[Response] = (db.actor ? InsertEntity(tbl, vs)).mapTo[Response]

  def minsert(tbl: String, kvs: Seq[Seq[BindValue]]): Future[Response] = kvs match {
    case x if x.isEmpty => Future.successful(ReturnID(0L))
    case _ =>
      val fs = kvs.grouped(db.batchSize).map { chunk =>
        (db.actor ? InsertEntities(tbl, chunk)).mapTo[Response]
      }
      Future.sequence(fs).map(res => errorOrElse[ReturnID](res.toSeq)(_.head))
  }


  def load(tbl: String, eids: Seq[Long], attrs: Seq[AttrSpec]): Future[Response] = {
    val fs = eids.grouped(db.batchSize).map { chunk =>
      (db.actor ? LoadEntities(tbl, chunk, attrs)).mapTo[Response]
    }

    Future.sequence(fs).map(res => errorOrElse[ValueLists](res.toSeq)(lists => ValueLists(lists.flatMap(_.vss))))
  }

  def load1(tbl: String, eid: Long, attrs: Seq[AttrSpec]) = (db.actor ? LoadEntity(tbl, eid, attrs)).mapTo[Response]

  def errorOrElse[T](list: Seq[Response])(fn: Seq[T] => Response): Response =
    list.find(_.isInstanceOf[Failed]).getOrElse(fn(list.map(_.asInstanceOf[T])))

  def select(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int, range: Int) =
    (db.actor ? Query(sql, cols, binds, start, range)).mapTo[Response]

  def update(tbl: String, ks: Seq[BindValue], vs: Seq[BindValue])(fn: ReturnID => Future[Response]) =
    (db.actor ? RemoveEntity(tbl, ks)) flatMap {
      case err: Failed => Future.successful(err)
      case res: ReturnID => fn(res)
    }

  def delete(tbl: String, ks: Seq[BindValue])(fn: ReturnID => Future[Response]): Future[Response] =
    (db.actor ? RemoveEntity(tbl, ks)) flatMap {
      case err: Failed => Future.successful(err)
      case res: ReturnID => fn(res)
    }

  
}

