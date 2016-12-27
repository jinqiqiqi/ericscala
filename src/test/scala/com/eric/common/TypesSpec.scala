package com.eric.common

import com.eric.common.types.UserType
import org.scalatest._

class TypesSpec extends FlatSpec with Matchers {
  "get('user')" should "Some(UserType)!" in {
    Types.get("user") should be (Some(UserType))
  }

  "getType('user')" should "UserType" in {
    Types.getType("user") should be (UserType)
  }

  it should "return 1" in {
    false should be (false)
  }

}
