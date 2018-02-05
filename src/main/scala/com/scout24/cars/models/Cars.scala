package com.scout24.cars.models

import com.scout24.cars.utils.Validation

//case Class that encapsulates Fuel
case class Fuel(name: String, renewable: Boolean) {
  def valid: Boolean = name.nonEmpty && (renewable.toString.toLowerCase == "true" || renewable.toString.toLowerCase == "false")
}
//Abstract class for CarId which is id of type Long
sealed abstract class CarId(id: Long) {
  lazy val carIdentification: CarIdentification = CarIdentification(id)
}
//case class to represent car identification
case class CarIdentification(id: Long) {
  override def toString: String = s"id=$id"
}
//case class that represents actual car object that will be registered from application to persistence.
//It has all the validation using require method
case class CarRegistration(
  id: Long,
  title: String,
  fuel: Fuel,
  price: Int,
  used: Boolean,
  mileage: Option[Int],
  registration: Option[String]) extends CarId(id) {

  require(id > 0, "Id should be positive number")
  require(title.nonEmpty, "Title should be provided for the car")
  require(fuel.valid, "Valid fuel type should be provided for the car.")
  require(price > 0, "Price should be a valid positive number")
  require(used.toString.toLowerCase == "true" || used.toString.toLowerCase == "false", "Used indication should only be true or false")
  require(if (used) mileage.nonEmpty && mileage.get > 0 else mileage.isEmpty, "Positive mileage should be provided if used car")
  require(if (used) registration.nonEmpty && Validation.isValidFormat(value = registration.get)
  else registration.isEmpty, "Date  provided for registration should be of " +
    "format 'dd/MM/yyyy' and should be less than current date if used car")

}
//case class that represents subset of car fields that comes in with request to update the existing car.
//It has all the validation using require method
case class CarUpdate(
  title: Option[String],
  fuel: Option[Fuel],
  price: Option[Int],
  used: Option[Boolean],
  mileage: Option[Int],
  registration: Option[String]) {

  require(if (title.nonEmpty) title.get.nonEmpty else title.isEmpty, "Title should be provided for the car")
  require(if (fuel.nonEmpty) fuel.get.valid else fuel.isEmpty, "Valid fuel type should be provided for the car.")
  require(if (price.nonEmpty) price.get > 0 else price.isEmpty, "Price should be a valid positive number")
  require(if (used.nonEmpty) used.get.toString.toLowerCase == "true" || used.get.toString.toLowerCase == "false"
  else used.isEmpty, "Used indication should only be true or false")

}

//Enum representing RegistrationResult of car registration
object RegistrationResult extends Enumeration {
  val RegistrationSuccessful, AlreadyExists = Value
}
//Enum representing UpdateRegistrationResult of the car
object UpdateRegistrationResult extends Enumeration {
  val UpdateSuccessful, NoSuchCar, InvalidUpdateForUsedCar, InvalidUpdateForNonUsedCar, InvalidMileage, InvalidRegistration, InvalidRegistrationOrMileage = Value
}
//Enum representing DeleteCarResult of the car
object DeleteCarResult extends Enumeration {
  val DeleteSuccessful, NoSuchCar = Value
}

