package com.eric.common

/**
  * Created by kinch on 12/20/16.
  */
trait Response

case class Failed(code: Int, msg: String) extends Response

case class User(uid: Long, nickname: String) extends Response
case class ValueList(vs: Map[String, String]) extends Response
