package com.eric.db

import java.sql._

import com.eric.common._
import org.slf4j.LoggerFactory

/**
  * Created by kinch on 12/21/16.
  */
case class DBUtil(c: Connection) extends DateUtil {

  private val log = LoggerFactory.getLogger(this.getClass)

  private def clock[T](sql: String)(fn: => T) = {
    val ts = System.nanoTime()
    log.info(sql)
    val res = fn
    val elapse = System.nanoTime() - ts
    if(elapse > 1000000)
      log.info(s"(${elapse.toString.dropRight(6)} ms)")
    else
      log.info(s"0.(${elapse.toString.dropRight(3)} ms)")
    res
  }

  private def statement[T](sql: String, fs: Int = 0)(fn: PreparedStatement => T) = {
    c.createStatement()
    val stmt = c.prepareStatement(sql)
    if (fs > 0) stmt.setFetchSize(fs)
    val res = fn(stmt)
    stmt.close()
    res
  }

  private def bind(stmt: PreparedStatement, binds: Seq[BindValue]) = {

  }

  private def cursor[T](stmt: PreparedStatement)(fn: ResultSet => T) = {
    val csr = stmt.executeQuery()
    val res = fn(csr)
    csr.close()
    res
  }

  private def fetchRows(stmt: PreparedStatement, cols: Seq[(String, Int)]): List[Map[String, String]] = {
    cursor(stmt) { csr =>
      def nullOrElse[T](n: String, v: T, default: String) =
        if(csr.wasNull()) (n, default) else (n, v.toString)

      Iterator.continually(csr.next()).takeWhile(x => x).map { _ =>
        (1 to cols.size).zip(cols).toSeq.map {
          case (idx, (n, Datatype.LONG_TYPE)) =>
            nullOrElse(n, csr.getLong(idx), "0")
          case (idx, (n, Datatype.INT_TYPE)) =>
            nullOrElse(n, csr.getInt(idx), "0")
          case (idx, (n, Datatype.STRING_TYPE)) =>
            nullOrElse(n, csr.getString(idx), "")
          case (idx, (n, Datatype.DOUBLE_TYPE)) =>
            nullOrElse(n, csr.getDouble(idx), "0")
          case (idx, (n, Datatype.BOOLEAN_TYPE)) =>
            nullOrElse(n, csr.getBoolean(idx), "0")
          case (idx, (n, Datatype.DATE_TYPE)) =>
            val ts = csr.getTimestamp(idx)
            if (csr.wasNull()) (n, defaultDatTime) else (n, seconds2str(ts.getTime/1000))
        }.toMap
      }.toList
    }
  }

  def select(sql: String, slist: Seq[(String, Int)], binds: Seq[BindValue]): List[Map[String, String]] = clock(sql) {
    statement(sql) { stmt =>
      bind(stmt, binds)
      fetchRows(stmt, slist)
    }
  }
}
