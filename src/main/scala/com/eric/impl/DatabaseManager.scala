package com.eric.impl


import akka.actor.{Actor, Props}
import com.eric.common._
import com.eric.db._
import com.eric.common.ReturnCode._
import javax.sql.DataSource

/**
  * Created by kinch on 12/21/16.
  */


class DatabaseManager(fs: Int)(implicit ds: DataSource) extends Actor with Transaction {

  val primary = "ID"

  private def select(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int, range: Int): Response =
    query { conn =>
      val offset = if (start > 0) s"offset $start" else ""
      val limit  = if (range > 0) s"limit $range" else ""
      ValueLists(DBUtil(conn).select(s"$sql $limit $offset", cols, binds))
    }

  private def load1(tbl: String, eid: Long, attrs: Seq[AttrSpec]) = query { conn =>
    DBUtil(conn).load1(tbl, attrs, primary, BindLong(primary, eid)) match {
      case x if x.isEmpty => report(ObjectNotFound, s"$eid")
      case res => ValueList(res)
    }
  }

  private def load(tbl: String, eids: Seq[Long], attrs: Seq[AttrSpec]) = query { conn =>
    ValueLists(DBUtil(conn).load(tbl, attrs, primary, eids.map(eid => BindLong(primary, eid))))
  }

  def receive = {
    case Query(sql, cols, binds, start, range) => sender ! select(sql, cols, binds, start, range)
    case LoadEntity(tbl, eid, attrs) => sender ! load1(tbl, eid, attrs)
    case LoadEntities(tbl, eids, attrs) => sender ! load(tbl, eids, attrs)
  }
}

object DatabaseManager {
  def props(fetchSize: Int)(implicit ds: DataSource) = Props{ new DatabaseManager(fetchSize) }
}
