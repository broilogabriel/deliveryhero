package com.deliveryhero.restaurant.service

import com.deliveryhero.restaurant.model.Restaurant

import scala.concurrent.Future

class RestaurantService extends Service[Int, Restaurant] {
  override def create(obj: Restaurant): Future[Option[Int]] = ???

  override def get: Future[Seq[Restaurant]] = ???

  override def getById(id: Int): Future[Option[Restaurant]] = ???

  override def update(id: Int): Future[String] = ???

  override def delete(id: Int): Future[String] = ???
}
