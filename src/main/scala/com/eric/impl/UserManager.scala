package com.eric.impl

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.eric.common._
import com.eric._
import com.eric.impl.model.UserModel

import scala.concurrent.Future

/**
  * Created by kinch on 12/20/16.
  */

class UserManager(batchSize: Int)(implicit to: Timeout) extends Actor {

  implicit val qm = QueryActorWrapper(context.system.actorSelection(DatabaseActor.actorPath))
  implicit val cm = CacheActorWrapper(context.system.actorSelection(CacheActor.actorPath))




  val userModel = UserModel()

  def receive = {
    case GetUser(uid) => getUser(uid).pipeTo(sender())
  }

  def getUser(uid: Long): Future[Response] = {
    userModel.profile(uid)
  }


}
object UserManager {
  def props(batchSize: Int)(implicit to: Timeout) = Props {
    new UserManager(batchSize)
  }
}
