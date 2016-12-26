package com.eric.impl

import akka.actor.{Actor, Props}
import com.eric.common._
import com.eric.db._
import javax.sql.DataSource

/**
  * Created by kinch on 12/21/16.
  */


class DatabaseManager(fs: Int)(implicit ds: DataSource) extends Actor with Transaction {

  private def select(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int, range: Int): Any =
    query { conn =>
      val offset = if (start > 0) s"offset $start" else ""
      val limit  = if (range > 0) s"limit $range" else ""
      println(s">> connection is: $conn")
      ValueLists(DBUtil(conn).select(s"$sql $limit $offset", cols, binds))
      

    }

  def receive = {
    case Query(sql, cols, binds, start, range) =>
      val msg = select(sql, cols, binds, start, range)
      println(s">> msg is: $msg")
      sender ! msg
  }

}

object DatabaseManager {
  def props(fetchSize: Int)(implicit ds: DataSource) = Props{ new DatabaseManager(fetchSize) }
}
