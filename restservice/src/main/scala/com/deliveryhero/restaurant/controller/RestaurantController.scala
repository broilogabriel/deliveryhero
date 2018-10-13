package com.deliveryhero.restaurant.controller

import akka.http.scaladsl.model
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, entity, pathPrefix, post, _}
import akka.http.scaladsl.server.{PathMatchers, Route}
import com.deliveryhero.restaurant.model.Restaurant
import com.deliveryhero.restaurant.service.Service
import com.typesafe.scalalogging.LazyLogging
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.{ArraySchema, Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.{GET, POST, PUT, Path}
import org.json4s.Formats
import org.json4s.jackson.Serialization.{read, write}

import scala.util.{Failure, Success, Try}

@Path("/v1/restaurants")
class RestaurantController(implicit formats: Formats, restaurantService: Service[Long, Restaurant]) extends LazyLogging {

  def routes: Route = {
    pathPrefix("restaurants") {
      get {
        getAll ~ getEntry
      } ~ createEntry ~ update
    }
  }

  @POST
  @Path("/")
  @Operation(summary = "Add Restaurant", description = "Creates a new restaurant", tags = Array("restaurant"),
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Restaurant])))),
    responses = Array(
      new ApiResponse(responseCode = "201",
        content = Array(new Content(schema = new Schema(implementation = classOf[String]))),
        headers = Array(new Header(name = HttpHeaders.LOCATION, description = "ID of the created Restaurant"))),
      new ApiResponse(responseCode = "400", description = "Input badly formatted"),
      new ApiResponse(responseCode = "500", description = "Internal Service Error")
    )
  )
  def createEntry: Route = post {
    entity(as[String])(body => {
      parse(body) { restaurant: Restaurant =>
        onComplete(restaurantService.create(restaurant)) {
          case Success(value) => value match {
            case None => complete(StatusCodes.BadRequest)
            case Some(id) =>
              respondWithHeaders(Location(model.Uri(id.toString))) {
                complete(StatusCodes.Created)
              }
          }
          case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
        }
      }
    })
  }

  @GET
  @Path("/")
  @Operation(summary = "Get all restaurants", description = "Returns a Sequence of all restaurants", tags = Array("restaurant"),
    responses = Array(
      new ApiResponse(responseCode = "200",
        content = Array(new Content(array = new ArraySchema(schema = new Schema(implementation = classOf[Restaurant]))))),
      new ApiResponse(responseCode = "500", description = "Internal Service Error")
    )
  )
  def getAll: Route = pathEnd {
    onComplete(restaurantService.get) {
      case Success(value) => complete(HttpEntity(ContentTypes.`application/json`, write(value)))
      case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
    }
  }

  @GET
  @Path("/{restaurantId}")
  @Operation(summary = "Get a restaurant by ID", description = "Returns a restaurant based on ID", tags = Array("restaurant"),
    parameters = Array(
      new Parameter(name = "restaurantId", in = ParameterIn.PATH, required = true, description = "ID of restaurant that needs to be fetched")
    ),
    responses = Array(
      new ApiResponse(responseCode = "200", content = Array(new Content(schema = new Schema(implementation = classOf[Restaurant])))),
      new ApiResponse(responseCode = "404", description = "Restaurant not found"),
      new ApiResponse(responseCode = "500", description = "Internal Service Error")
    )
  )
  def getEntry: Route = path(PathMatchers.Segments) { segments =>
    parse[Long, Route](segments.head) { id =>
      onComplete(restaurantService.getById(id)) {
        case Success(value) => value match {
          case Some(restaurant) => complete(HttpEntity(ContentTypes.`application/json`, write(restaurant)))
          case None => complete(StatusCodes.NotFound)
        }
        case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
      }
    }
  }

  @PUT
  @Path("/{restaurantId}")
  @Operation(summary = "Update restaurant", description = "Updates an existing restaurant", tags = Array("restaurant"),
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Restaurant])))),
    parameters = Array(
      new Parameter(name = "restaurantId", in = ParameterIn.PATH, required = true, description = "ID of restaurant that needs to be updated")
    ),
    responses = Array(
      new ApiResponse(responseCode = "204", content = Array(new Content(schema = new Schema(implementation = classOf[String])))),
      new ApiResponse(responseCode = "400", content = Array(new Content(schema = new Schema(implementation = classOf[String])))),
      new ApiResponse(responseCode = "500", description = "Internal Service Error")
    )
  )
  def update: Route = put {
    path(PathMatchers.Segments) { segments =>
      parse[Long, Route](segments.head) { id =>
        entity(as[String]) { restaurantUpdate =>
          parse[Restaurant, Route](restaurantUpdate) { update =>
            onComplete(restaurantService.update(id, update)) {
              case Success(statusCode) => complete(statusCode)
              case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
            }
          }
        }
      }
    }
  }

  def parse[T, V <: Route](str: String)(block: T => Route)(implicit m: Manifest[T]): Route = Try(read[T](str)) match {
    case Success(restaurant) => block(restaurant)
    case Failure(ex) => complete((StatusCodes.BadRequest))
  }

}
