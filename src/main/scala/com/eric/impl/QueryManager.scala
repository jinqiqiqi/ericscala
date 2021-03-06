package com.eric.impl

import akka.actor.{ Actor, Props }
import akka.pattern.pipe
import akka.util.Timeout
import com.eric.DatabaseActor
import com.eric.common.{ BindValue, DBActorWrapper, Query, Response, DBOperations }
// import com.eric.common._
import scala.concurrent.Future



class QueryManager(batchSize: Int, limit: Int)(implicit to: Timeout) extends Actor {

  import context.dispatcher

  implicit val dbActor = DBActorWrapper(context.system.actorSelection(DatabaseActor.actorPath), batchSize)
  val database = DBOperations()

  def receive = {
    case Query(sql, cols, binds, start, range) => run(sql, cols, binds, start, range).pipeTo(sender())
  }

  private def run(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int = 0, range: Int = 0): Future[Response] = {
    database.select(sql, cols, binds, start, if(range == 0) limit else range)
  }

}

object QueryManager {
  def props(batchSize: Int, limit: Int)(implicit to: Timeout) = Props {
    new QueryManager(batchSize, limit)
  }
}
