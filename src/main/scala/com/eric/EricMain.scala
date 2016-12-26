package com.eric


import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import com.eric.http.EricListener
import com.typesafe.config.ConfigFactory
import spray.can.Http



object EricMain extends App {

  val config = ConfigFactory.load().getConfig("eric.service")

  implicit val system = ActorSystem(Actors.actorSystem)

  val host = config.getConfig("http").getString("host")
  val port = config.getConfig("http").getInt("port")

  implicit val timeout = Timeout(config.getInt("asyncTimeout").seconds)

  val api = system.actorOf(Props(new EricListener(config, system)), "EricListener")
  IO(Http) ! Http.Bind(listener = api, interface = host, port = port)

}

