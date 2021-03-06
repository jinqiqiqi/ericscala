package com.eric.impl

import akka.actor.{ Actor, Props }
import com.eric._
import com.eric.common._


/**
 * Created by kinch on 12/20/16.
 */

case class DispatcherManager() extends Actor {
  val userManager = context.system.actorSelection(UserActor.actorPath)
  val cacheManager = context.system.actorSelection(CacheActor.actorPath)
  val entityManager = context.system.actorSelection(EntityActor.actorPath)

  def receive = {
    // user related messages
    case msg: GetUser => userManager.forward(msg)
    case msg: Login => userManager.forward(msg)
    case msg: GetEntity => entityManager.forward(msg)
    case msg: GetEntities => entityManager.forward(msg)

  }
}

object DispatcherManager {

  def props = Props { new DispatcherManager() }
}
