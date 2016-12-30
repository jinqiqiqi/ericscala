package com.eric.common

trait Response

// request result case class
case class Failed(code: Int, msg: String) extends Response

// User staff
case class User(uid: Long, nickname: String) extends Response

case class UserProfile(uid: Long, profile: Map[String, String]) extends Response

// data type case class
case class ValueList(vs: Map[String, String]) extends Response

case class ValueLists(vss: Seq[Map[String, String]]) extends Response
case class Entities(ens: Seq[Entity]) extends Response

case class ReturnID(eid: Long) extends Response



// cache stuff
case class CachedValue(v: String) extends Response
case class CachedValues(vs: Seq[String]) extends Response
case class CachedBoolean(v: Boolean) extends Response
case class CachedHValues(vs: Map[String, Long]) extends Response
