package com.eric.common

import spray.json.DefaultJsonProtocol

/**
  * Created by kinch on 12/20/16.
  */
object Serialize extends DefaultJsonProtocol {
  implicit val failed = jsonFormat2(Failed.apply)
}
