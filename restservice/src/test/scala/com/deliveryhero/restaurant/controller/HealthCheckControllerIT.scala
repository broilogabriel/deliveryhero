package com.deliveryhero.restaurant.controller

import com.deliveryhero.restaurant.{AtomicLongProvider, RestaurantsRoute}
import com.deliveryhero.restaurant.service.RestaurantService
import org.apache.http.client.fluent.Request
import org.json4s.DefaultFormats
import org.scalamock.scalatest.MockFactory
import org.scalatest._

class HealthCheckControllerIT extends FunSpec with Matchers with MockFactory with BeforeAndAfter with BeforeAndAfterAll {
  implicit val formats = DefaultFormats
  implicit val jsonFilePath = Some("/restaurants.json")
  implicit val idProvider = new AtomicLongProvider
  implicit val restaurantService = new RestaurantService(jsonFilePath)
  implicit val restaurantController = new RestaurantController
  implicit val healthCheckController = new HealthCheckController

  private val TestPort = 1234
  private val BaseUrl = s"http://localhost:$TestPort"
  private val HealthCheckEndpoint = s"$BaseUrl/v1/healthcheck"

  val restaurantsRoute = new RestaurantsRoute()

  override protected def beforeAll(): Unit = {
    restaurantsRoute.startService(TestPort)
  }

  describe("Restaurant") {
    it("Should respond OK") {
      inSequence {
        healthCheckController.ping
      }

      val actual = Request
        .Get(HealthCheckEndpoint)
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 200
    }
  }


}
