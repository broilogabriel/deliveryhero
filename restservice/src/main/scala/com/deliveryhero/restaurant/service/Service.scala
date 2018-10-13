package com.deliveryhero.restaurant.service

import akka.http.scaladsl.model.StatusCode

import scala.concurrent.Future

trait Service[K, T] {
  def create(obj: T): Future[Option[K]]

  def get: Future[Seq[T]]

  def getById(id: K): Future[Option[T]]

  def update(id: K, elem: T): Future[StatusCode]

  def delete(id: K): Future[String]
}
