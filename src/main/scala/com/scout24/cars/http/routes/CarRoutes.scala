package com.scout24.cars.http.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive0, Route }
import com.scout24.cars.models.{ CarIdentification, CarRegistration, CarUpdate }
import com.scout24.cars.services.CarService
import com.scout24.cars.utils.Protocol

class CarRoutes(carService: CarService) extends Protocol {
  val carRoutes = pathPrefix("car" / "id" / LongNumber) { (id) =>
    val urlIdentifiers = CarIdentification(id)
    pathEndOrSingleSlash {
      post { //Register a car
        entity(as[CarRegistration]) { carRegistration =>
          registerCar(carRegistration, urlIdentifiers)
        }
      } ~
        patch { //Update a car
          entity(as[CarUpdate]) { carUpdate =>
            updateCar(carUpdate, urlIdentifiers)
          }
        } ~
        get { //Retrieve information about the car
          retrieveCar(urlIdentifiers)
        } ~
        delete { //Delete information about the car
          deleteCar(urlIdentifiers)
        }
    }
  } ~ pathPrefix("car" / "all" / Segment.?) { (orderBy) =>
    pathEndOrSingleSlash {
      get {
        getAllCars(orderBy)
      }
    }
  }

  private def registerCar(carRegistration: CarRegistration, urlIdentifiers: CarIdentification): Route = {
    validateEquals(urlIdentifiers, carRegistration.carIdentification) {
      val saveResult = carService.save(carRegistration)
      import com.scout24.cars.models.RegistrationResult._
      onSuccess(saveResult) {
        case RegistrationSuccessful => complete("car registered")
        case AlreadyExists => complete(Conflict, "car already exists")
      }
    }
  }

  private def updateCar(carUpdate: CarUpdate, urlIdentifiers: CarIdentification): Route = {

    val saveResult = carService.update(carUpdate, urlIdentifiers)
    import com.scout24.cars.models.UpdateRegistrationResult._
    onSuccess(saveResult) {
      case UpdateSuccessful => complete("car updated")
      case NoSuchCar => notFound(urlIdentifiers)
      case InvalidUpdateForNonUsedCar => complete(Conflict, "Invalid update on non-used car")
      case InvalidUpdateForUsedCar => complete(Conflict, "Invalid update on used car")
      case InvalidMileage => complete(Conflict, "Invalid value update for mileage")
      case InvalidRegistration => complete(Conflict, "Invalid value update for registration")
      case InvalidRegistrationOrMileage => complete(Conflict, "Invalid value update for mileage or registration")
    }

  }

  private def retrieveCar(urlIdentifiers: CarIdentification): Route = {
    val carInfo = carService.read(urlIdentifiers)
    onSuccess(carInfo) {
      case Some(x) => complete(x)
      case None => notFound(urlIdentifiers)
    }
  }

  private def deleteCar(urlIdentifiers: CarIdentification): Route = {
    val deleteResult = carService.delete(urlIdentifiers)
    import com.scout24.cars.models.DeleteCarResult._
    onSuccess(deleteResult) {
      case DeleteSuccessful => complete(s"Deleted car with id $urlIdentifiers")
      case NoSuchCar => notFound(urlIdentifiers)
    }
  }

  private def getAllCars(orderBy: Option[String]): Route = {
    val allCars = carService.readAll(orderBy)
    onSuccess(allCars) {
      l => complete(l)
    }
  }

  private def notFound(CarIdentification: CarIdentification): Route = {
    complete(NotFound, s"Could not find the car identified by: $CarIdentification")
  }

  private def validateEquals(urlIdentifiers: CarIdentification, bodyIdentifiers: CarIdentification): Directive0 = {
    validate(urlIdentifiers == bodyIdentifiers, s"resource identifiers from the path [$urlIdentifiers] and the body: [$bodyIdentifiers] do not match")
  }
}

object CarRoutes {
  def apply(): CarRoutes = new CarRoutes(CarService())
}
