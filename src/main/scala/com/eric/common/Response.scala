package com.eric.common

trait Response

// request result case class
case class Failed(code: Int, msg: String) extends Response

// data case class
case class User(uid: Long, nickname: String) extends Response

// data type case class
case class ValueList(vs: Map[String, String]) extends Response

case class ValueLists(vss: Seq[Map[String, String]]) extends Response

case class ReturnID(eid: Long) extends Response

