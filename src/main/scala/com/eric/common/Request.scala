package com.eric.common 

/**
  * Created by kinch on 12/20/16.
  */
trait Request


// User manager messages
case class GetUser(uid: Long) extends Request
case class Login(uid: Long) extends Request

// query operations
case class Query(sql: String, cols: Seq[(String, Int)], binds: Seq[BindValue], start: Int = 0, range: Int = -1)


// database operations
case class LoadEntity(dbTable: String, eid: Long, cols: Seq[AttrSpec]) extends Request
case class LoadEntities(dbTable: String, eids: Seq[Long], cols: Seq[AttrSpec]) extends Request
case class InsertEntity(dbTable: String, kvs: Seq[BindValue]) extends Request
case class RemoveEntity(dbTable: String, ks: Seq[BindValue]) extends Request
case class InsertEntities(dbTable: String, kvs: Seq[Seq[BindValue]]) extends Request



// cache operations
case class CacheGet(k: String, db: Int = 0)
case class CacheMGet(kvs: Seq[String], db: Int = 0)
case class CacheSet(k: String, v: String, exp: Int = 0, db: Int = 0)
case class CacheMSet(kvs: Seq[(String, String)], db: Int = 0)
case class CacheIncr(k: String, by: Int, db: Int = 0)
case class CacheRemove(ks: Seq[String], db: Int = 0)
case class CacheExists(k: String, db: Int = 0)
case class CacheHMSet(k: String, vs: Seq[(String, Long)], db: Int = 0)
case class CacheHMGet(k: String, vs: Seq[String], db: Int = 0)
case class CacheHIncr(k: String, f: String, by: Int = 1, exp: Int = 0, db: Int = 0)
case class CacheHDel(k: String, f: String, db: Int = 0)
case class CacheHGetAll(k: String, db: Int = 0)

// Refresh redis cache entities
case class PinCache(db: Int, ks: Seq[String], force: Boolean = true) extends Request


// Entities cache staff
case class GetEntity(tn: String, eid: Long) extends Request
case class GetEntities(tn: String, eids: String) extends Request
case class CreateEntity(tn: String, kvs: Map[String, String]) extends Request
case class UpdateEntity(tn: String, ks: Map[String, String], vs: Map[String, String]) extends Request
case class DeleteEntities(tn: String, ks: Map[String, String]) extends Request
case class CreateEntities(tn: String, kvs: Seq[Map[String, String]]) extends Request
case class DeleteAndCreate(tn: String, ks: Map[String, String], kvs: Seq[Map[String, String]]) extends Request
case class GetEntityType(tn: String) extends Request
