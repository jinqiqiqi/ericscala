package com.eric

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import scala.concurrent.duration.DurationInt

import akka.actor.{ExtendedActorSystem, Extension, ExtensionKey}
import akka.routing.FromConfig
import akka.util.Timeout
import com.eric.impl._
import com.eric.common._
import com.redis.RedisClientPool


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

  val cacheServers = {
    val dbcnt = config.getInt("cacheServer.dbCount")
    val pwd = config.getString("cacheServer.password")
    val hn = config.getString("cacheServer.host")
    val pn = config.getInt("cacheServer.port")
    Range(0, dbcnt).map { idx =>
      (idx, RedisCache(new RedisClientPool(host = hn, port = pn, database = idx, secret = if(pwd.isEmpty()) None else Some(pwd) )))
    }.toMap
  }

  private val batchSize = config.getInt("batchSize")
  private val fetchLimit = config.getInt("fetchLimit")
  private val flushSize = config.getInt("flushSize")



  //create all actors
  system.actorOf(DispatcherManager.props, DispatcherActor.actorName)

  system.actorOf(FromConfig.props(DatabaseManager.props(batchSize)), DatabaseActor.actorName)
  system.actorOf(FromConfig.props(QueryManager.props(batchSize, fetchLimit)), QueryActor.actorName)
  system.actorOf(FromConfig.props(UserManager.props(batchSize)), UserActor.actorName)
  system.actorOf(FromConfig.props(CacheManager.props(cacheServers, flushSize, batchSize)), CacheActor.actorName)
}
object Eric extends ExtensionKey[EricExt]
