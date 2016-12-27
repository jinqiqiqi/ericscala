package com.eric.common.types

import com.eric.common.Datatype._
import com.eric.common.Types
import com.eric.common.Constants._


case object UserType extends Types {
  val tn = "user"
  val dbTable = "users"
  val attrs = Seq(
    (Attr.ID, Attr.ID, LONG_TYPE),
    (Attr.USERNAME, Attr.USERNAME, STRING_TYPE),
    (Attr.PASSWORD, Attr.PASSWORD, STRING_TYPE),
    (Attr.AGE, Attr.AGE, LONG_TYPE),
    (Attr.PROVINCE, Attr.PROVINCE, STRING_TYPE)

  )
}
