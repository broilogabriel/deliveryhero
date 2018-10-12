package com.deliveryhero.restaurant.service

import scala.concurrent.Future

trait Service[K, T] {
  def create(obj: T): Future[Option[K]]

  def get: Future[Seq[T]]

  def getById(id: K): Future[Option[T]]

  def update(id: K): Future[String]

  def delete(id: K): Future[String]
}
