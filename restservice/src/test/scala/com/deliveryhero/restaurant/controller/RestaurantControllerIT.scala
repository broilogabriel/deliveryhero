package com.deliveryhero.restaurant.controller

import akka.http.scaladsl.model.StatusCodes
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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class RestaurantControllerIT extends FunSpec with Matchers with MockFactory with BeforeAndAfter with BeforeAndAfterAll {
  implicit val formats = DefaultFormats
  implicit val atomicLongIdProvider = mock[IdProvider[Long]]
  implicit val restaurantService = mock[RestaurantService]
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
      (restaurantService.create _).expects(defaultRestaurant).returning(Future(Some(15)))

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
      val restaurantNonUnicode = defaultRestaurant.copy(name = "Герой доставки")
      (restaurantService.create _).expects(restaurantNonUnicode).returning(Future(Some(15)))

      val inputBody = write[Restaurant](restaurantNonUnicode)

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
        (restaurantService.get _).expects().returning(Future(Seq(defaultRestaurant)))

        val expected = write(Seq(defaultRestaurant))

        val actual = Request
          .Get(RestaurantsEndpoint)
          .execute()
          .returnContent()
          .asString()
        actual shouldBe expected
      }
    }

    describe("ID") {
      it("Should return specific restaurant") {
        val restaurant1 = defaultRestaurant.copy(id = Some(111))
        restaurant1.id.map(id => {
          (restaurantService.getById _).expects(id)
            .returning(Future(Some(restaurant1)))

          val expected = write(restaurant1)

          val value = s"$RestaurantsEndpoint/$id"
          val actual = Request
            .Get(value)
            .execute()
            .returnContent()
            .asString()
          actual shouldBe expected
        })
      }

      it("Should return 404 for missing ID") {
        val requestId = 123
        (restaurantService.getById _).expects(requestId)
          .returning(Future(None))

        val value = s"$RestaurantsEndpoint/$requestId"
        val actual = Request
          .Get(value)
          .execute()
          .returnResponse()
        actual.getStatusLine.getStatusCode shouldBe 404
      }
    }
  }

  describe("PUT") {
    it("should replace existing restaurant") {
      defaultRestaurant.copy(id = Some(1)).id.map(id => {
        val restaurantUpdate = defaultRestaurant.copy(phoneNo = "087-987-6543")
        (restaurantService.update _).expects(id, restaurantUpdate).returning(Future(StatusCodes.NoContent))

        val inputBody = write(restaurantUpdate)

        val actual = Request
          .Put(s"$RestaurantsEndpoint/$id")
          .bodyString(inputBody, ContentType.APPLICATION_JSON)
          .execute()
          .returnResponse()
        actual.getStatusLine.getStatusCode shouldBe 204
      })
    }

    it("should return 404 if restaurant ID not found") {
      val updateId = 123
      (restaurantService.update _).expects(updateId, defaultRestaurant).returning(Future(StatusCodes.NotFound))

      val inputBody = write(defaultRestaurant)

      val actual = Request
        .Put(s"$RestaurantsEndpoint/$updateId")
        .bodyString(inputBody, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 404
    }

    it("should return 400 if update json is not a restaurant object") {
      val updateId = 123
      val inputBody = """{"name":"thing"}"""

      val actual = Request
        .Put(s"$RestaurantsEndpoint/$updateId")
        .bodyString(inputBody, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 400
    }
  }

  describe("Delete") {
    it("should delete existing variable") {
      defaultRestaurant.copy(id = Some(1)).id.map(id => {
        (restaurantService.delete _).expects(id).returning(Future(StatusCodes.NoContent))

        val actual = Request
          .Delete(s"$RestaurantsEndpoint/$id")
          .execute()
          .returnResponse()

        actual.getStatusLine.getStatusCode shouldBe 204
      })
    }

    it("should return 404 if nothing to delete") {
      val updateId: Long = 123
      (restaurantService.delete _).expects(updateId).returning(Future(StatusCodes.NotFound))

      val actual = Request
        .Delete(s"$RestaurantsEndpoint/$updateId")
        .execute()
        .returnResponse()
      actual.getStatusLine.getStatusCode shouldBe 404
    }
  }

}
