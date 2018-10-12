package com.deliveryhero.restaurant.controller

import com.deliveryhero.restaurant.RestaurantsRoute
import com.deliveryhero.restaurant.model.{Address, Restaurant}
import com.deliveryhero.restaurant.service.RestaurantService
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import org.scalamock.scalatest.MockFactory
import org.scalatest._

class RestaurantControllerIT extends FunSpec with Matchers with MockFactory with BeforeAndAfter with BeforeAndAfterAll {
  implicit val formats = DefaultFormats
  implicit val restaurantService = new RestaurantService()
  implicit val restaurantController = new RestaurantController
  implicit val healthCheckController = new HealthCheckController

  private val TestPort = 1234
  private val BaseUrl = s"http://localhost:$TestPort"
  private val RestaurantsEndpoint = s"$BaseUrl/v1/restaurants"

  val restaurantsRoute = new RestaurantsRoute()

  override protected def beforeAll(): Unit = {
    restaurantsRoute.startService(TestPort)
  }

  describe("Restaurant") {
    it("Should save restaurant") {
      val restaurant = Restaurant("Zaytoon", "087-123-4567",
        Seq("Persian", "Middle Eastern"),
        Address("13 Parliament Street", "Temple Bar", "Dublin", "Ireland"),
        "The home of amazing Persian Cuisine")
      inSequence {
        (restaurantService.create _).expects(restaurant)
      }

      val inputBody = write[Restaurant](restaurant)

      val actual = Request
        .Post(RestaurantsEndpoint)
        .bodyString(inputBody, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 201
    }
  }


}
