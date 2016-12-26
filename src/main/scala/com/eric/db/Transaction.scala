package com.eric.db

import com.eric.common.ReturnCode._
import java.sql.Connection
import javax.sql.DataSource

import com.eric.common.Response

import scala.util.control.NonFatal

/**
  * Created by kinch on 12/21/16.
  */
trait Transaction {

  def connection(fn: Connection => Response)(implicit ds: DataSource) =
    try {
      val conn = ds.getConnection()
      try fn(conn)
      finally conn.close()
    }
    catch {
      case NonFatal(e) => report(DatabaseCallError, e.getMessage)
    }

  def query(fn: Connection => Response)(implicit ds: DataSource) = connection(c => fn(c))
}
