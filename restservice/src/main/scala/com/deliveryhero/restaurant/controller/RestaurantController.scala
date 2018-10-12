package com.deliveryhero.restaurant.controller

import akka.http.scaladsl.model
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, entity, pathPrefix, post, _}
import akka.http.scaladsl.server.Route
import com.deliveryhero.restaurant.model.Restaurant
import com.deliveryhero.restaurant.service.Service
import com.typesafe.scalalogging.LazyLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.ws.rs.{POST, Path}
import org.json4s.Formats
import org.json4s.jackson.Serialization.{read, write}

import scala.util.{Failure, Success}

@Path("/restaurants")
class RestaurantController(implicit formats: Formats, restaurantService: Service[Int, Restaurant]) extends LazyLogging {

  def routes: Route = {
    pathPrefix("restaurants") {
      createEntry
    }
  }

  @POST
  @Path("/")
  @Operation(summary = "Add Restaurant", description = "Creates a new restaurant", tags = Array("restaurant"),
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Restaurant])))),
    responses = Array(
      new ApiResponse(responseCode = "201", content = Array(new Content(schema = new Schema(implementation = classOf[String])))),
      new ApiResponse(responseCode = "400", description = "Input badly formatted"),
      new ApiResponse(responseCode = "500", description = "Internal Service Error")
    )
  )
  def createEntry: Route = post {
    entity(as[String])(body => {
      onComplete(restaurantService.create(read[Restaurant](body))) {
        case Success(value) => value match {
          case None => complete(StatusCodes.BadRequest)
          case Some(id) => respondWithHeaders(Location(model.Uri(id.toString))) {
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, write(id.toString)))
          }
        }
        case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
      }
    })
  }
}