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

case object UserActor extends Actors {
  val name = "USER"
  val actorName = "UserManager"
}

case object DatabaseActor extends Actors {
  val name ="DATABASE"
  val actorName = "DBRouter"
}

case object CacheActor extends Actors {
  val name = "CACHE"
  val actorName = "CacheManager"
}

