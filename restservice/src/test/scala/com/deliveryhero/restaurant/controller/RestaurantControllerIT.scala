package com.deliveryhero.restaurant.controller

import com.deliveryhero.restaurant.model.{Address, Restaurant}
import com.deliveryhero.restaurant.service.RestaurantService
import com.deliveryhero.restaurant.{IdProvider, RestaurantsRoute}
import javax.ws.rs.core.HttpHeaders
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import org.scalamock.scalatest.MockFactory
import org.scalatest._

class RestaurantControllerIT extends FunSpec with Matchers with MockFactory with BeforeAndAfter with BeforeAndAfterAll {
  implicit val formats = DefaultFormats
  implicit val atomicLongIdProvider = mock[IdProvider[Long]]
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

  val defaultRestaurant = Restaurant(None, "Zaytoon", "087-123-4567",
    Seq("Persian", "Middle Eastern"),
    Address("13 Parliament Street", "Temple Bar", "Dublin", "Ireland"),
    "The home of amazing Persian Cuisine")

  describe("POST create") {
    it("Should save a correctly formatted restaurant") {
      (atomicLongIdProvider.getNextId _).expects().returning(15)

      val inputBody = write[Restaurant](defaultRestaurant)

      val actual = Request
        .Post(RestaurantsEndpoint)
        .bodyString(inputBody, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 201
      actual.getFirstHeader(HttpHeaders.LOCATION).getValue.toLong shouldBe 15
    }

    it("Should handle non unicode characters") {
      (atomicLongIdProvider.getNextId _).expects().returning(15)

      val inputBody = write[Restaurant](defaultRestaurant.copy(name = "Герой доставки"))

      val actual = Request
        .Post(RestaurantsEndpoint)
        .bodyString(inputBody, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse()

      actual.getStatusLine.getStatusCode shouldBe 201
      actual.getFirstHeader(HttpHeaders.LOCATION).getValue.toLong shouldBe 15
    }

    it("Should return 400 if json is not a Restaurant object") {
      val inputBody =
        """
          |{"description": "I'm json, but I'm not a restaurant!}"
          |""".stripMargin

      val actual = Request
        .Post(RestaurantsEndpoint)
        .bodyString(inputBody, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 400
    }

    it("Should return 400 if POST is empty") {
      val actual = Request
        .Post(RestaurantsEndpoint)
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 400
    }


    it("Should return 400 if non json string received") {
      val inputBody =
        """
          |Delivery Hero!"
          |""".stripMargin

      val actual = Request
        .Post(RestaurantsEndpoint)
        .bodyString(inputBody, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 400
    }
  }

  describe("GET") {
    describe("All") {
      it("Should return all existing restaurants") {
        val inputBody = write[Restaurant](defaultRestaurant)
        Request
          .Post(RestaurantsEndpoint)
          .bodyString(inputBody, ContentType.APPLICATION_JSON)
          .execute()

        val expected = write[Restaurant](defaultRestaurant)

        val actual = Request
          .Get(RestaurantsEndpoint)
          .execute()
          .returnContent()
          .asString()
        actual shouldBe expected
      }

    }
  }

}
