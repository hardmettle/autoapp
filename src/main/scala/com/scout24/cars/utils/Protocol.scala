package com.scout24.cars.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.scout24.cars.models.{CarRegistration, CarUpdate, Fuel}
import spray.json.{DefaultJsonProtocol, PrettyPrinter}

trait Protocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val printer = PrettyPrinter
  implicit val fuelFormat = jsonFormat2(Fuel)
  implicit val carRegistrationFormat = jsonFormat7(CarRegistration)
  implicit val carUpdateFormat = jsonFormat6(CarUpdate)

}
