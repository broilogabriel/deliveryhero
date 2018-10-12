package com.deliveryhero.restaurant

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, getFromResourceDirectory, options, pathPrefix, pathSingleSlash, redirect, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.deliveryhero.restaurant.controller.RestaurantController
import com.deliveryhero.restaurant.service.SwaggerDocService
import org.json4s.Formats

import scala.concurrent.ExecutionContextExecutor

class RestaurantsRoute(implicit formats: Formats, restaurantController: RestaurantController) {
  val DefaultPort = 8087

  implicit val system: ActorSystem = ActorSystem("deliveryhero-restaurants")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def startService(port: Int = DefaultPort): Unit = {

    def assets = pathPrefix("swagger") {
      getFromResourceDirectory("swagger") ~ pathSingleSlash(get(redirect("index.html", StatusCodes.PermanentRedirect)))
    }

    val routes: Route =
      assets ~
        restaurantController.routes ~ SwaggerDocService.routes ~
        options {
          complete(s"Supported methods : GET, POST, PUT, DELETE")
        }

    val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", port)

    sys.addShutdownHook {
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  }

}
