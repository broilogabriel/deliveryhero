package com.deliveryhero.restaurant

import com.deliveryhero.restaurant.controller.RestaurantController
import com.deliveryhero.restaurant.service.RestaurantService
import com.typesafe.scalalogging.LazyLogging
import org.json4s.DefaultFormats

class Main extends App with LazyLogging {
  logger.info("Restaurant service starting...")

  implicit val formats = DefaultFormats
  implicit val restaurantService = new RestaurantService()
  implicit val restaurantController = new RestaurantController
  val restaurantsRoute = new RestaurantsRoute

  restaurantsRoute.startService _

}
