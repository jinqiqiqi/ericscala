package com.eric.common

/**
  * Created by kinch on 12/21/16.
  */
object ReturnCode {
  def report(code: Int, p: String): Failed = code match {
    case DatabaseCallError => Failed(code, s"Database Call Error ($p)")
  }

  val DatabaseCallError = 100
}


