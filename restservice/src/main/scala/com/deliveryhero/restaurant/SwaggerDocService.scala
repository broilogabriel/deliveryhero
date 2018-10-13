package com.deliveryhero.restaurant

import com.deliveryhero.restaurant.controller.{HealthCheckController, RestaurantController}
import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.{Contact, Info}

object SwaggerDocService extends SwaggerHttpService {
  override def apiClasses: Set[Class[_]] = Set(classOf[RestaurantController], classOf[HealthCheckController])

  override def host: String = "localhost:8080"

  override def apiDocsPath: String = "api-docs"

  override def unwantedDefinitions: Seq[String] = Seq("Function1RequestContextFutureRouteResult", "SeqString")

  override def info: Info = Info(
    description = "API for Delivery Hero's Restaurants",
    version = "0.1",
    title = "Delivery Hero",
    contact = Some(Contact(name = "Shane Murphy", url = "", email = "smur89@gmail.com"))
  )
}
