package com.eric.impl

import akka.actor.{ Actor, Props }
import com.eric._
import com.eric.common._

/**
 * Created by kinch on 12/20/16.
 */

case class Dispatcher() extends Actor {
  val userManager = context.system.actorSelection(UserActor.actorPath)
  val cacheManager = context.system.actorSelection(CacheActor.actorPath)

  def receive = {
    case msg: GetUser => userManager.forward(msg)
  }
}

object Dispatcher {

  def props = Props { new Dispatcher() }
}
