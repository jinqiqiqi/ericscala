package com.eric.common

/**
  * Created by kinch on 12/21/16.
  */
object ReturnCode {
  def report(code: Int, p: String): Failed = code match {
    case DatabaseCallError => Failed(code, s"Database Call Error ($p)")
    case ObjectNotFound => Failed(code, s"Object Not Found ($p)")
    case TypeNotFound => Failed(code, s"Type Not Found ($p)")
    case InvalidQueryID => Failed(code, s"Invalid Query ID ($p)")
    case InvalidBindVariables => Failed(code, s"Invalid Bind Variables ($p)")
    case QueryOutOfRange => Failed(code, s"Query out of range ($p)")
    case OrderByPositionOutOfRange => Failed(code, s"Order by position out of range ($p)")
    case MissingParameters => Failed(code, s"Missing parameters ($p)")
    case BanWordViolation => Failed(code, s"BanWordViolation ($p)")
    case InvalidAttributes => Failed(code, s"InvalidAttributes ($p)")
    case ObjectDeleted => Failed(code, s"ObjectDeleted ($p)")
    case AlreadyMember => Failed(code, s"AlreadyMember ($p)")
    case NoneMember => Failed(code, s"NoneMember ($p)")
    case IsAdmin => Failed(code, s"IsAdmin ($p)")
    case InsufficientPoints => Failed(code, s"InsufficientPoints ($p)")
    case Creator => Failed(code, s"Creator ($p)")
    case AccessDenied => Failed(code, s"AccessDenied ($p)")
    case ElasticIndexError => Failed(code, s"ElasticIndexError ($p)")
    case InvalidUserID => Failed(code, s"InvalidUserID ($p)")
      
  }

  val DatabaseCallError = 100
  val ObjectNotFound = 101
  val TypeNotFound = 102
  val InvalidQueryID = 103
  val InvalidBindVariables = 104
  val QueryOutOfRange = 105
  val OrderByPositionOutOfRange = 106
  val MissingParameters = 107
  val BanWordViolation = 108
  val InvalidAttributes = 109
  val ObjectDeleted = 110
  val AlreadyMember = 111
  val NoneMember = 112
  val IsAdmin = 113
  val InsufficientPoints = 114
  val Creator = 115
  val AccessDenied = 116
  val ElasticIndexError = 117
  val InvalidUserID = 118
}


