package com.deliveryhero.restaurant.controller

import akka.http.scaladsl.model
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, get, path, pathEnd, pathPrefix, post, put, _}
import akka.http.scaladsl.server.{PathMatchers, Route}
import com.deliveryhero.restaurant.model.Restaurant
import com.deliveryhero.restaurant.service.Service
import com.typesafe.scalalogging.LazyLogging
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{ArraySchema, Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import javax.ws.rs.{POST, Path, _}
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

  //
  //  @GET
  //  @Path("/")
  //  @Operation(summary = "Get all restaurants", description = "Returns a Sequence of all restaurants", tags = Array("restaurant"),
  //    responses = Array(
  //      new ApiResponse(responseCode = "200",
  //        content = Array(new Content(array = new ArraySchema(schema = new Schema(implementation = classOf[Restaurant])))))
  //    )
  //  )
  //  def getAll: Route = pathEnd {
  //    logger.info("Getting restaurants")
  //    complete(HttpEntity(ContentTypes.`application/json`, write(restaurantService.get)))
  //  }
  //
  //  @GET
  //  @Path("/{restaurantId}")
  //  @Operation(summary = "Get a restaurant by ID", description = "Returns a restaurant based on ID", tags = Array("restaurant"),
  //    parameters = Array(
  //      new Parameter(name = "restaurantId", in = ParameterIn.PATH, required = true, description = "ID of restaurant that needs to be fetched")
  //    ),
  //    responses = Array(
  //      new ApiResponse(responseCode = "200", content = Array(new Content(schema = new Schema(implementation = classOf[Restaurant]))))
  //    )
  //  )
  //  def getEntry: Route = path(PathMatchers.Segments) { segments =>
  //    val name = segments.head
  //    logger.info(s"Getting restaurant $name")
  //    val response = restaurantService.getById(name)
  //      .fold(BackendResponse[Restaurant](errors = List(s"restaurant $name not found"))) { restaurant =>
  //        BackendResponse[Restaurant](body = Seq(restaurant))
  //      }
  //    complete(HttpEntity(ContentTypes.`application/json`, write(response)))
  //  }
  //
  //  @PUT
  //  @Path("/{restaurantId}")
  //  @Operation(summary = "Update restaurant", description = "Updates an existing restaurant", tags = Array("restaurant"),
  //    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Restaurant])))),
  //    parameters = Array(
  //      new Parameter(name = "restaurantId", in = ParameterIn.PATH, required = true, description = "ID of restaurant that needs to be updated")
  //    ),
  //    responses = Array(
  //      new ApiResponse(responseCode = "204", content = Array(new Content(schema = new Schema(implementation = classOf[String])))),
  //      new ApiResponse(responseCode = "400", content = Array(new Content(schema = new Schema(implementation = classOf[String]))))
  //    )
  //  )
  //  def updateEntry: Route = put {
  //    path(PathMatchers.Segments) { segments =>
  //      val oldName = segments.head
  //      entity(as[String]) { restaurantJson =>
  //        restaurantService.update(oldName, read[Restaurant](restaurantJson))
  //      }
  //    }
  //  }
  //
  //  @DELETE
  //  @Path("/{restaurantId}")
  //  @Operation(summary = "Delete restaurant", description = "Deletes an existing restaurant", tags = Array("restaurant"),
  //    parameters = Array(
  //      new Parameter(name = "restaurantId", in = ParameterIn.PATH, required = true, description = "ID of restaurant to be deleted")
  //    ),
  //    responses = Array(
  //      new ApiResponse(responseCode = "200", content = Array(new Content(schema = new Schema(implementation = classOf[String]))))
  //    )
  //  )
  //  def deleteEntry(): Route = delete {
  //    path(PathMatchers.Segments) { segments =>
  //      val existingrestaurant = segments.head
  //      restaurantService.delete(existingrestaurant)
  //    }
  //  }
}