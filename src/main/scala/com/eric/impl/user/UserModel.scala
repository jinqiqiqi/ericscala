package com.eric.impl.user

import scala.concurrent.{ExecutionContext, Future}

import akka.util.Timeout
import com.eric.common._
import com.eric.common.Constants._


/**
  * Created by kinch on 12/21/16.
  */
case class UserAttrs(implicit qm: QueryActorWrapper, cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout) {

  def profile(uid: Long) = {
    val sql = "select id, username, password, age, province from user"
    val cols = Seq((Attr.ID, Datatype.LONG_TYPE), (Attr.USERNAME, Datatype.STRING_TYPE), (Attr.PASSWORD, Datatype.STRING_TYPE), (Attr.AGE, Datatype.INT_TYPE), (Attr.PROVINCE, Datatype.STRING_TYPE))
    val binds = Seq.empty
    QueryOps.query(sql, cols, binds)(vss => Future.successful(ValueLists(vss)))
  }
}
