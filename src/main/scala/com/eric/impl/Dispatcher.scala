package com.eric.impl

import akka.actor.{Actor, Props}
import com.eric.UserActor
import com.eric.common.GetUser

/**
  * Created by kinch on 12/20/16.
  */

case class Dispatcher() extends Actor {
  val userManager = context.system.actorSelection(UserActor.actorPath)

  def receive = {
    case msg: GetUser => userManager.forward(msg)
  }
}

object Dispatcher {

  def props = Props {
    new Dispatcher()
  }
}
