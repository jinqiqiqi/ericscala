package com.eric.impl.user

import scala.concurrent.{ExecutionContext, Future}

import akka.util.Timeout
import com.eric.common._
import com.eric.common.Constants.Attr
import com.eric.common.types.UserType


/**
  * Created by kinch on 12/21/16.
  */
case class UserAttrs(implicit qm: QueryActorWrapper, cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout) {

  def profile(uid: Long): Future[Response] = {

    QueryOps.simpleQuery(UserType, Seq(Attr.ID, Attr.USERNAME, Attr.PASSWORD, Attr.AGE), s"id = ?", Seq(BindLong(Attr.ID, uid))){
      case vss if vss.isEmpty => Future.successful(ReturnCode.report(ReturnCode.InvalidUserID, uid.toString))
      case (vs :: _) =>
        Future.successful(UserProfile(uid, vs))
    }

  }
}
