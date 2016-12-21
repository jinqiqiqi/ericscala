package com.eric.http

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.Config
import spray.routing.{HttpService, HttpServiceActor}

/**
  * Created by kinch on 12/20/16.
  */
class EricListener(config: Config, system: ActorSystem)(implicit timeout: Timeout)
  extends HttpServiceActor with HttpService with Endpoints with ActorLogging { actor: Actor =>
  implicit val ec = context.dispatcher
  def receive = runRoute(routes(system))

}
