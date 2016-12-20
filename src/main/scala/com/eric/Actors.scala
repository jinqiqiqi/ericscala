package com.eric

/**
  * Created by kinch on 12/20/16.
  */

sealed trait Actors {
  def name: String
  def actorName: String
  def actorPath = s"akka://${Actors.actorSystem}/user/${actorName}"
}
object Actors {
  val actorSystem = "Eric"
}

case object DispatcherActor extends Actors {
  val name = "DISPATCHER"
  val actorName = "Dispatcher"
}
