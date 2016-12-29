package com.eric.common.types

import com.eric.common.Constants.Attr
import com.eric.common.Types
import com.eric.common.Datatype._

/**
  * Created by kinch on 12/29/16.
  */
case object KeyValueType extends Types{
  val tn = "KEYVALUE"
  val dbTable = "redis_results"

  override val attrs: Seq[(String, String, Int)] = Seq(
    (Attr.KEY, "REDIS_KEY", STRING_TYPE),
    (Attr.CACHEDB, "REDIS_DB", INT_TYPE),
    (Attr.TYPE, "KEY_TYPE", STRING_TYPE),
    (Attr.VALUE, "REDIS_VALUE", STRING_TYPE),
    (Attr.MODIFIED, "MODIFIED", DATE_TYPE)
  )
}
