package com.scout24.cars.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ HttpEntity, MediaTypes }
import com.scout24.cars.ApiSpec
import com.scout24.cars.http.routes.CarRoutes
import com.scout24.cars.models.{ CarIdentification, CarRegistration, CarUpdate }
import com.scout24.cars.services.CarServiceImpl
import com.scout24.cars.services.dao.CarsDao
import com.scout24.cars.utils.Protocol
import spray.json.JsValue
import spray.json._

class HttpServiceTest extends ApiSpec with Protocol {
  val httpService = new HttpService(
    new CarRoutes(new CarServiceImpl(CarsDao())))

  import TestData._

  "service" should {

    s"respond with HTTP-$NotFound for a non existing path" in {
      Get("/non/existing/") ~> httpService.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "The path you requested [/non/existing/] does not exist."
      }
    }

    s"respond with HTTP-$MethodNotAllowed for a non supported HTTP method" in {
      Head(generalUrl()) ~> httpService.routes ~> check {
        status shouldBe MethodNotAllowed
        responseAs[String] shouldBe "Not supported method! Supported methods are: POST, PATCH, GET, DELETE!"
      }
    }

  }

  "registration" should {
    s"respond with HTTP-$OK when registering a new car" in {
      val carRegistration = carInfo()
      val jsonString = if (carRegistration.used) registrationJsonUsed(carRegistration) else registrationJsonNonUsed(carRegistration)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, jsonString)
      Post(generalUrl(carRegistration.carIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[String] shouldBe "car registered"
      }
    }

    s"respond with HTTP-$Conflict when trying to register an existing car" in {
      val existingCar = carInfo()
      dao.create(existingCar)
      val jsonString = if (existingCar.used) registrationJsonUsed(existingCar) else registrationJsonNonUsed(existingCar)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, jsonString)
      Post(generalUrl(existingCar.carIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe Conflict
        responseAs[String] shouldBe "car already exists"
      }
    }

    s"respond with HTTP-$Forbidden failing validation if path and body resource identifiers are different" in {
      val pathIdentifiers = carInfo().carIdentification
      val bodyRegistration = carInfo()
      val jsonString = if (bodyRegistration.used) registrationJsonUsed(bodyRegistration) else registrationJsonNonUsed(bodyRegistration)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, jsonString)
      Post(generalUrl(pathIdentifiers), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe Forbidden
        responseAs[String] shouldBe
          s"validation failed: resource identifiers from the path [$pathIdentifiers] " +
          s"and the body: [${bodyRegistration.carIdentification}] do not match"
      }
    }

  }

  "update" should {

    s"respond with HTTP-$OK when updating an existing car" in {
      val existingCar = carInfo()
      dao.create(existingCar)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, updateJsonNonUsed())
      Patch(generalUrl(existingCar.carIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[String] shouldBe "car updated"
      }
    }
    s"respond with HTTP-$Forbidden wrong update on car" in {
      val existingCar = carInfo()
      dao.create(existingCar)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, updateJsonConflict())
      Patch(generalUrl(existingCar.carIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe Forbidden
      }
    }
    s"respond with HTTP-$Conflict wrong update on non-used car" in {
      val existingCar = carInfo().copy(used = false, mileage = None, registration = None)
      dao.create(existingCar)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, updateJsonConflictNonUsed())
      Patch(generalUrl(existingCar.carIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe Conflict
      }
    }

    s"respond with HTTP-$Conflict wrong update on used car" in {
      val existingCar = carInfo().copy(used = true, mileage = Some(1), registration = Some("11/11/2011"))
      dao.create(existingCar)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, updateJsonConflictUsed())
      Patch(generalUrl(existingCar.carIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe Conflict
      }
    }
    s"respond with HTTP-$NotFound for a non existing car" in {
      val notExistingId = carInfo().carIdentification
      val requestEntity = HttpEntity(MediaTypes.`application/json`, updateJsonNonUsed())
      Patch(generalUrl(notExistingId), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe NotFound
        responseAs[String] shouldBe s"Could not find the car identified by: $notExistingId"
      }
    }
  }

  "retrieval" should {

    s"respond with HTTP-$OK for an existing car" in {
      val existingCar = carInfo()
      dao.create(existingCar)
      Get(generalUrl(existingCar.carIdentification)) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[JsValue] shouldBe retrieveJson(existingCar)
      }
    }

    s"respond with HTTP-$NotFound for a non existing car" in {
      val notExistingId = carInfo().carIdentification
      Get(generalUrl(notExistingId)) ~> httpService.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe s"Could not find the car identified by: $notExistingId"
      }
    }

  }

  object TestData {

    def retrieveJson(carRegistration: CarRegistration): JsValue =
      if (carRegistration.used) {
        s"""
           |{
           |
           |		"id":${carRegistration.id},
           |		"title":"${carRegistration.title}",
           |		"fuel":{"name":"${carRegistration.fuel.name}", "renewable":${carRegistration.fuel.renewable}},
           |		"price":${carRegistration.price},
           |		"used":true,
           |		"mileage":${carRegistration.mileage.get},
           |		"registration":"${carRegistration.registration.get}"
           |
         |}""".stripMargin.parseJson
      } else {
        s"""
           |{
           |
           |		"id":${carRegistration.id},
           |		"title":"${carRegistration.title}",
           |		"fuel":{"name":"${carRegistration.fuel.name}", "renewable":${carRegistration.fuel.renewable}},
           |		"price":${carRegistration.price},
           |		"used":false
           |
          |}""".stripMargin.parseJson
      }

    def registrationJsonUsed(carRegistration: CarRegistration = carInfo()): String =
      s"""
         |{
         |
         |		"id":${carRegistration.id},
         |		"title":"${carRegistration.title}",
         |		"fuel":{"name":"${carRegistration.fuel.name}", "renewable":${carRegistration.fuel.renewable}},
         |		"price":${carRegistration.price},
         |		"used":true,
         |		"mileage":12,
         |		"registration":"10/12/2011"
         |
         |}""".stripMargin.parseJson.toString

    def registrationJsonNonUsed(carRegistration: CarRegistration = carInfo()): String =
      s"""
         |{
         |
         |		"id":${carRegistration.id},
         |		"title":"${carRegistration.title}",
         |		"fuel":{"name":"${carRegistration.fuel.name}", "renewable":${carRegistration.fuel.renewable}},
         |		"price":${carRegistration.price},
         |		"used":false
         |
         |}""".stripMargin.parseJson.toString

    def updateJsonNonUsed(): String =
      s"""
         |{
         |		"title":"new update title"
         |}
         |""".stripMargin.parseJson.toString

    def updateJsonConflict(): String =
      s"""
         |{
         |		"price":-1
         |}
         |""".stripMargin.parseJson.toString

    def updateJsonConflictNonUsed(): String =
      s"""
         |{
         |		"mileage":1
         |}
         |""".stripMargin.parseJson.toString

    def updateJsonConflictUsed(): String =
      s"""
         |{
         |		"used":false
         |}
         |""".stripMargin.parseJson.toString

    def generalUrl(carIdentification: CarIdentification = carInfo().carIdentification): String =
      s"/v1/car/id/${carIdentification.id}"

  }

}

