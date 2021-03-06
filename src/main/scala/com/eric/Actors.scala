package com.eric

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
  val actorName = "DispatcherManager"
}

case object UserActor extends Actors {
  val name = "USER"
  val actorName = "UserManager"
}

case object DatabaseActor extends Actors {
  val name ="DATABASE"
  val actorName = "DatabaseManager"
}

case object CacheActor extends Actors {
  val name = "CACHE"
  val actorName = "CacheManager"
}

case object QueryActor extends Actors {
  val name = "QUERY"
  val actorName = "QueryManager"
}

case object EntityActor extends Actors {
  val name = "ENTITY"
  val actorName = "EntityManager"
}