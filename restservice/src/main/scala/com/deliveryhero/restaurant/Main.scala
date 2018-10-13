package com.deliveryhero.restaurant

import com.deliveryhero.restaurant.controller.{HealthCheckController, RestaurantController}
import com.deliveryhero.restaurant.service.RestaurantService
import com.typesafe.scalalogging.LazyLogging
import org.json4s.DefaultFormats

object Main extends App with LazyLogging {

  implicit val formats = DefaultFormats
  implicit val idProvider = AtomicLongProvider
  implicit val restaurantService = new RestaurantService(Some("/opt/deliveryhero/config/restaurants.json"))
  implicit val restaurantController = new RestaurantController
  implicit val healthCheckController = new HealthCheckController
  val restaurantsRoute = new RestaurantsRoute
  logger.info("Restaurant service starting...")

  restaurantsRoute.startService()

}
