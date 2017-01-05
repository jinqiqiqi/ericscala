package com.eric.common

import spray.json.DefaultJsonProtocol

/**
  * Created by kinch on 12/20/16.
  */ 
object Serialize extends DefaultJsonProtocol {
  implicit val failed = jsonFormat2(Failed.apply)
  implicit val valuelists = jsonFormat1(ValueLists.apply)  
  implicit val returnid = jsonFormat1(ReturnID.apply)


  implicit val entity = jsonFormat3(Entity.apply)
  implicit val entities = jsonFormat1(Entities.apply)

  // user staff
  implicit val userprofile = jsonFormat2(UserProfile.apply)
  implicit val login = jsonFormat1(Login.apply)
}
