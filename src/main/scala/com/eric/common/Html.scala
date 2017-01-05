package com.eric.common

import scala.util.matching.Regex


case class Html(s: String) {
  val imageTag: Regex = "<img([^>]+)>".r
  
}
