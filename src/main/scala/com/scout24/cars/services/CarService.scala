package com.scout24.cars.services

import com.scout24.cars.models._
import com.scout24.cars.services.dao.CarsDao
import com.scout24.cars.utils
import com.scout24.cars.utils.{ OrderingCar, Validation }

import scala.concurrent.Future

//Trait representing service layer to do car related operation
trait CarService {
  /**
   * Save method that takes car registration and saves via dao in table
   * @param carRegistration
   * @return RegistrationResult
   */
  def save(carRegistration: CarRegistration): Future[RegistrationResult.Value]

  /**
   * Update method that takes CarIdentification to find car and update with CarUpdate using dao object
   * @param carUpdate
   * @param carIdentification
   * @return UpdateRegistrationResult
   */
  def update(carUpdate: CarUpdate, carIdentification: CarIdentification): Future[UpdateRegistrationResult.Value]
  /**
   * Read method that takes CarIdentification to find car using dao object
   * @param carIdentification
   * @return Option of CarRegistration
   */
  def read(carIdentification: CarIdentification): Future[Option[CarRegistration]]

  /**
   * Delete method that takes CarIdentification to delete car using dao object
   * @param carIdentification
   * @return DeleteCarResult
   */
  def delete(carIdentification: CarIdentification): Future[DeleteCarResult.Value]

  /**
   * Method that returns all the car using dao object. Optionally order by provide parameter else defaults ordering to id
   * @param orderBy
   * @return List of CarRegistration
   */
  def readAll(orderBy: Option[String]): Future[List[CarRegistration]]
}

//Companion object for CarService that instantiates CarServiceImpl
object CarService {
  def apply(): CarService = new CarServiceImpl(CarsDao())
}

//Concrete implementation of CarService and its meothods
class CarServiceImpl(dao: CarsDao) extends CarService with utils.ActorContext {

  override def save(carRegistration: CarRegistration): Future[RegistrationResult.Value] = {
    val existingCar = read(carRegistration.carIdentification)

    def save = Future {
      dao.create(carRegistration)
      RegistrationResult.RegistrationSuccessful
    }

    existingCar flatMap {
      case Some(x) => Future.successful(RegistrationResult.AlreadyExists)
      case None => save

    }
  }

  override def update(carUpdate: CarUpdate, carIdentification: CarIdentification): Future[UpdateRegistrationResult.Value] = {
    val existingCar = read(carIdentification)
    existingCar flatMap {
      case Some(x) =>

        var updatedCarRegistration = x

        /**
         * Validations being done before update for eg :
         * - InvalidUpdateForNonUsedCar if used updated as false with value updated for mileage and registration
         * - InvalidUpdateForUsedCar if updating used from true to false
         */

        carUpdate match {
          case CarUpdate(_, _, _, Some(used), Some(mileage), Some(registration)) if !used => Future {
            UpdateRegistrationResult.InvalidUpdateForNonUsedCar
          }
          case CarUpdate(_, _, _, None, Some(mileage), Some(registration)) if !x.used => Future {
            UpdateRegistrationResult.InvalidUpdateForNonUsedCar
          }
          case CarUpdate(_, _, _, Some(used), None, Some(registration)) if !used => Future {
            UpdateRegistrationResult.InvalidUpdateForNonUsedCar
          }
          case CarUpdate(_, _, _, None, None, Some(registration)) if !x.used => Future {
            UpdateRegistrationResult.InvalidUpdateForNonUsedCar
          }
          case CarUpdate(_, _, _, Some(used), Some(mileage), None) if !used => Future {
            UpdateRegistrationResult.InvalidUpdateForNonUsedCar
          }
          case CarUpdate(_, _, _, None, Some(mileage), None) if !x.used => Future {
            UpdateRegistrationResult.InvalidUpdateForNonUsedCar
          }
          case CarUpdate(_, _, _, Some(used), _, _) if !used && x.used => Future {
            UpdateRegistrationResult.InvalidUpdateForUsedCar
          }
          case CarUpdate(_, _, _, _, Some(mileage), None) if mileage < 0 => Future {
            UpdateRegistrationResult.InvalidMileage
          }
          case CarUpdate(_, _, _, _, None, Some(registration)) if !Validation.isValidFormat(value = registration) => Future {
            UpdateRegistrationResult.InvalidRegistration
          }
          case CarUpdate(_, _, _, _, Some(mileage), Some(registration)) if mileage < 0 || !Validation.isValidFormat(value = registration) => Future {
            UpdateRegistrationResult.InvalidRegistrationOrMileage
          }

          case _ =>
            updatedCarRegistration = updatedCarRegistration.copy(
              title = carUpdate.title.getOrElse(x.title),
              fuel = carUpdate.fuel.getOrElse(x.fuel), price = carUpdate.price.getOrElse(x.price), used = carUpdate.used.getOrElse(x.used), mileage = if (carUpdate.mileage.nonEmpty)
                carUpdate.mileage else x.mileage, registration = if (carUpdate.registration.nonEmpty)
                carUpdate.registration else x.registration)

            dao.update(updatedCarRegistration).map(_ => UpdateRegistrationResult.UpdateSuccessful)
        }

      case None => Future.successful(UpdateRegistrationResult.NoSuchCar)
    }
  }

  override def read(carIdentification: CarIdentification): Future[Option[CarRegistration]] = {
    dao.read(carIdentification)
  }
  override def readAll(orderBy: Option[String]): Future[List[CarRegistration]] = {
    val allCars = dao.readAll()
    allCars map {
      cars =>
        {
          orderBy match {
            case Some(o) => o.toLowerCase match {
              case "id" => cars.toList.sortBy(c => c.id)
              case "title" => cars.toList.sortBy(c => c.title)
              case "fuelname" => cars.toList.sortBy(c => c.fuel.name)
              case "fuelrenewable" => cars.toList.sortBy(c => c.fuel.renewable)
              case "price" => cars.toList.sortBy(c => c.price)
              case "used" => cars.toList.sortBy(c => c.used)
              case "mileage" => cars.toList.sortBy(c => c.mileage)
              case "registration" => cars.toList.sortWith((c1, c2) => OrderingCar.orderCarWithRegistrationDate(c1, c2))
              case _ => cars.toList.sortBy(c => c.id)
            }
            case None => cars.toList.sortBy(c => c.id)
          }
        }
    }
  }
  override def delete(carIdentification: CarIdentification): Future[DeleteCarResult.Value] = {
    val existingCar = read(carIdentification)
    existingCar flatMap {
      case Some(x) => dao.delete(carIdentification).map(_ => DeleteCarResult.DeleteSuccessful)
      case None => Future.successful(DeleteCarResult.NoSuchCar)
    }
  }
}
