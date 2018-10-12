package com.deliveryhero.restaurant.service

import com.deliveryhero.restaurant.controller.RestaurantController
import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.{Contact, Info}

object SwaggerDocService extends SwaggerHttpService {
  override def apiClasses: Set[Class[_]] = Set(classOf[RestaurantController])

  override def host: String = "localhost:8080"

  override def apiDocsPath: String = "api-docs"

  override def unwantedDefinitions: Seq[String] = Seq("Function1RequestContextFutureRouteResult")

  override def info: Info = Info(
    description = "API for Delivery Hero's Restaurants",
    version = "0.1",
    title = "Delivery Hero",
    contact = Some(Contact(name = "Shane Murphy", url = "", email = "smur89@gmail.com"))
  )
}