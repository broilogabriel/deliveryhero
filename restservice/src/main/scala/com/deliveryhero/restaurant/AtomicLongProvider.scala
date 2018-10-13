package com.deliveryhero.restaurant

import java.util.concurrent.atomic.AtomicLong

trait IdProvider[T] {
  def getNextId: T
}

object AtomicLongProvider extends IdProvider[Long] {

  val atomicLong = new AtomicLong(0)

  def getNextId = atomicLong.getAndIncrement()

}
