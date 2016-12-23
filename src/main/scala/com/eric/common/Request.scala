package com.eric.common 

/**
  * Created by kinch on 12/20/16.
  */
trait Request


// user actions
case class GetUser(uid: Long) extends Request



// query operations
case class Query(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int = 0, range: Int = -1)


// database operations
case class LoadEntities(dbTable: String, eids: Seq[Long], cols: Seq[AttrSpec])
