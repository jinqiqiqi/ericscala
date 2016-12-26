package com.eric.impl


import akka.actor.{Actor, Props}
import akka.pattern._
import akka.util.Timeout

import com.eric._
import com.eric.common._
import com.eric.impl.user.UserAttrs

import scala.concurrent.Future
/**
  * Created by kinch on 12/20/16.
  */

class UserManager(batchSize: Int)(implicit to: Timeout) extends Actor {

  import context.dispatcher

  implicit val qm = QueryActorWrapper(context.system.actorSelection(QueryActor.actorPath))
  implicit val cm = CacheActorWrapper(context.system.actorSelection(CacheActor.actorPath))

  val userAttrs = UserAttrs()

  def receive = {
    case GetUser(uid) => getUser(uid).pipeTo(sender())
  }

  def getUser(uid: Long): Future[Response] = {
    userAttrs.profile(uid)
  }


}

//
object UserManager {
  def props(batchSize: Int)(implicit to: Timeout) = Props {
    new UserManager(batchSize)
  }
}
