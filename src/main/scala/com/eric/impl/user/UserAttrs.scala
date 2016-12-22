package com.eric.impl.user

import akka.util.Timeout
import com.eric.common._
import com.eric.commom.QueryOps

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by kinch on 12/21/16.
  */
case class UserAttrs(implicit qm: QueryActorWrapper, cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout) {

  def profile(uid: Long) = {
    val sql = "select * from users"
    val cols = Seq((Attr.USER, Datatype.LONG_TYPE), (Attr.DISPLAYNAME, Datatype.STRING_TYPE))
    val binds = Seq.empty
    QueryOps.query(sql, cols, binds)(vss => Future.successful(ValueList(vss)))
  }
}
