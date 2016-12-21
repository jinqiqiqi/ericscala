package com.eric.impl.model

import akka.util.Timeout
import com.eric.commom.QueryOps
import com.eric.common._

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by kinch on 12/21/16.
  */
case class UserModel(implicit qm: QueryActorWrapper, cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout) {
  def profile(uid: Long) = {
    val sql = "select * from users"
    val cols = Seq((Attr.USER, Datatype.LONG_TYPE), (Attr.DISPLAYNAME, Datatype.STRING_TYPE))
    val binds = Seq.empty
    QueryOps.query(sql, cols, binds)(vss => Future.successful(ValueList(vss))).map {
      case err: Failed => Left(err)
      case ValueList(x) => x.map(u => (u(Attr.USER)))
    }
  }
}
