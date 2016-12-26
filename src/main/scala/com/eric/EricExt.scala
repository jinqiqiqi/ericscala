package com.eric

import akka.actor.{ExtendedActorSystem, Extension, ExtensionKey}
import akka.routing.FromConfig
import akka.util.Timeout
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource

import scala.concurrent.duration.DurationInt

import com.eric.impl._


/**
  * Created by kinch on 12/20/16.
  */

class EricExt(system: ExtendedActorSystem) extends Extension {

  val config = system.settings.config.getConfig("eric.service")
  implicit val timeout = Timeout(config.getInt("asyncTimeout").seconds)
  implicit val ds = {
    val mysql = new MysqlDataSource
    mysql.setURL(config.getString("dataSource.url"))
    mysql.setUser(config.getString("dataSource.user"))
    mysql.setPassword(config.getString("dataSource.password"))
    mysql.setCharacterEncoding(config.getString("dataSource.character"))
    mysql
  }

  private val batchSize = config.getInt("batchSize")
  private val fetchLimit = config.getInt("fetchLimit")


  //create all actors
  system.actorOf(DispatcherManager.props, DispatcherActor.actorName)

  system.actorOf(FromConfig.props(DatabaseManager.props(batchSize)), DatabaseActor.actorName)
  system.actorOf(FromConfig.props(QueryManager.props(batchSize, fetchLimit)), QueryActor.actorName)
  system.actorOf(FromConfig.props(UserManager.props(batchSize)), UserActor.actorName)
}
object Eric extends ExtensionKey[EricExt]
