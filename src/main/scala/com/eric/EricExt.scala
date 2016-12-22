package com.eric

import akka.actor.{ExtendedActorSystem, Extension, ExtensionKey}
import akka.routing.FromConfig
import akka.util.Timeout
import com.eric.impl.UserManager

import scala.concurrent.duration.DurationInt

import com.eric.impl._


/**
  * Created by kinch on 12/20/16.
  */

class EricExt(system: ExtendedActorSystem) extends Extension {

  val config = system.settings.config.getConfig("eric.service")
  implicit val timeout = Timeout(config.getInt("asyncTimeout").seconds)
  private val batchSize = config.getInt("batchSize")


  //create all actors
  system.actorOf(Dispatcher.props, DispatcherActor.actorName)
  system.actorOf(FromConfig.props(UserManager.props(batchSize)), UserActor.actorName)
}
object Eric extends ExtensionKey[EricExt]{

}
