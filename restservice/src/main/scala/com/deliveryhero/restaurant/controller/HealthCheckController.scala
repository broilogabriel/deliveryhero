package com.deliveryhero.restaurant.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, pathPrefix, _}
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.ws.rs.{Path, _}

@Path("v1/healthcheck")
class HealthCheckController extends LazyLogging {

  def routes: Route = {
    pathPrefix("healthcheck") {
      ping
    }
  }

  @GET
  @Path("/")
  @Operation(summary = "Health Check", description = "Returns 200 OK if service is up",
    responses = Array(
      new ApiResponse(responseCode = "200")
    )
  )
  def ping: Route = {
    complete(StatusCodes.OK)
  }

}
