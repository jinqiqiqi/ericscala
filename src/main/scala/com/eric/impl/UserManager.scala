package com.eric.impl

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.eric.common.{GetUser, Response}

import scala.concurrent.Future

/**
  * Created by kinch on 12/20/16.
  */

class UserManager(implicit to: Timeout) extends Actor {

  def getUser(uid: Long): Future[Response] = {

  }

  def receive = {
    case GetUser(uid) => getUser(uid).pipeTo(sender())
  }
}
object UserManager {
  def props(implicit to: Timeout) = Props {
    new UserManager
  }
}
