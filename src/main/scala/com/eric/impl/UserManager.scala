package com.eric.impl

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.{Actor, Props}
import akka.pattern._
import akka.util.Timeout
import com.eric.common._
import com.eric.impl.user.UserAttrs

/**
  * Created by kinch on 12/20/16.
  */

class UserManager(batchSize: Int)(implicit qm: QueryActorWrapper, cm: CacheActorWrapper, ec: ExecutionContext, to: Timeout) extends Actor {

  val userModel = UserAttrs()

  def receive = {
    case GetUser(uid) => getUser(uid).pipeTo(sender())
  }

  def getUser(uid: Long): Future[Response] = {
    userModel.profile(uid)
  }


}

//
object UserManager {
  def props(batchSize: Int) = Props {
    new UserManager(batchSize)
  }
}
