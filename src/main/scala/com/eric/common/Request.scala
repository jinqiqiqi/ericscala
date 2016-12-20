package com.eric.common

/**
  * Created by kinch on 12/20/16.
  */
trait Request

case class GetUser(uid: Long) extends Response