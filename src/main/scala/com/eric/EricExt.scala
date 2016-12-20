package com.eric

import akka.actor.{ExtendedActorSystem, Extension, ExtensionKey}
import akka.routing.FromConfig
import com.eric.impl.UserManager

/**
  * Created by kinch on 12/20/16.
  */

class EricExt(system: ExtendedActorSystem) extends Extension {

  val config = system.settings.config.getConfig("eric.service")

  system.actorOf(FromConfig.props(UserManager.props), UserActor.actorName)
}
object Eric extends ExtensionKey[EricExt]{

}
