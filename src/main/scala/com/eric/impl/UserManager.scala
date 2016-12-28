package com.eric.impl


import com.eric.common._
import com.eric.impl.user.UserAttrs

import scala.concurrent.Future
import akka.actor.{Actor, Props}
import akka.pattern._
import akka.util.Timeout
import com.eric._



class UserManager(batchSize: Int)(implicit to: Timeout) extends Actor {

  import context.dispatcher

  implicit val qm = QueryActorWrapper(context.system.actorSelection(QueryActor.actorPath))
  implicit val cm = CacheActorWrapper(context.system.actorSelection(CacheActor.actorPath))

  val userAttrs = UserAttrs()

  def receive = {
    case GetUser(uid) => getUser(uid).pipeTo(sender())
    case Login(uid) => login(uid).pipeTo(sender())
  }

  def login(uid: Long): Future[Response] = {
    // KeyType.pin(UserStats(uid), LatestChatsVisitedBy(uid), UsersReferredBy(uid), LatestTopicsReferredTo(uid), ReferToCount(uid), ReferToFloor(uid), BookmarkTopicByUser(uid))

    // MostSigninsPerUserDaily.zincr(uid)
    // MostSigninsPerUserMonthly.zincr(uid)
    userAttrs.profile(uid)
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
