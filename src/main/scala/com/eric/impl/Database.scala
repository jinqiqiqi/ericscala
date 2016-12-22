package com.eric.impl

import javax.sql.DataSource

import akka.actor.Actor
import com.eric.common.{BindValue, Query, ValueList}
import com.eric.db.{DBUtil, Transaction}

/**
  * Created by kinch on 12/21/16.
  */


class Database(fs: Int)(implicit ds: DataSource) extends Actor with Transaction {

  private def select(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int, range: Int): Any =
    query { conn =>
      val offset = if (start > 0) s"offset $start" else ""
      val limit  = if (range > 0) s"limit $range" else ""
      ValueList(DBUtil(conn).select(s"$sql $limit $offset", cols, binds))

    }

  def receive = {
    case Query(sql, cols, binds, start, range) => sender ! select(sql, cols, binds, start, range)
  }


}