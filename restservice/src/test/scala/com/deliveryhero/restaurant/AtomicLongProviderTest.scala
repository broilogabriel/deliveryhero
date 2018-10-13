package com.deliveryhero.restaurant

import org.scalatest.{FunSuite, Matchers}

class AtomicLongProviderTest extends FunSuite with Matchers {

  test("next Id should increment Id") {
    val id = AtomicLongProvider.getNextId
    id shouldBe 0
    val id2 = AtomicLongProvider.getNextId
    id2 shouldBe 1
  }

}
