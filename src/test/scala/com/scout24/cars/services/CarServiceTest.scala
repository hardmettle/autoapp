package com.scout24.cars.services

import com.scout24.cars.UnitSpec
import com.scout24.cars.models.CarUpdate
import com.scout24.cars.services.dao.CarsDao

import scala.concurrent.Future

class CarServiceTest extends UnitSpec {
  private val existingCar = carInfo(1)
  private val existingNonUsedCar = carInfo().copy(used = false, mileage = None, registration = None)
  private val existingUsedCar = carInfo().copy(used = true, mileage = Some(1), registration = Some("11/11/2011"))

  private val updateExistingCar = CarUpdate(Some(existingCar.title), Some(existingCar.fuel),
    Some(existingCar.price), Some(existingCar.used), existingCar.mileage, existingCar.registration)
  private val updateExistingNonUsedCar = CarUpdate(None, None, None, None, Some(1), Some("11/11/2011"))
  private val updateExistingUsedCar = CarUpdate(None, None, None, Some(false), None, None)

  private val nonExistingCar = carInfo(2)
  "read" should {

    "read an existing entry" in new TestFixture {
      service.read(existingCar.carIdentification).futureValue.value shouldBe existingCar
    }

    "return NONE for a non existing entry" in new TestFixture {
      service.read(nonExistingCar.carIdentification).futureValue shouldBe None
    }

    "save" should {
      import com.scout24.cars.models.RegistrationResult._
      s"return $RegistrationSuccessful for a new entry" in new TestFixture {
        service.save(nonExistingCar).futureValue shouldBe RegistrationSuccessful
      }

      s"return $AlreadyExists for an existing entry" in new TestFixture {
        service.save(existingCar).futureValue shouldBe AlreadyExists
      }
    }

    "update" should {
      import com.scout24.cars.models.UpdateRegistrationResult._
      s"return $UpdateSuccessful for an existing entry of car" in new TestFixture {
        service.update(updateExistingCar, existingCar.carIdentification).futureValue shouldBe UpdateSuccessful
      }
      s"return $InvalidUpdateForNonUsedCar for an existing entry of non used car" in new TestFixture {
        service.update(updateExistingNonUsedCar, existingNonUsedCar.carIdentification).futureValue shouldBe InvalidUpdateForNonUsedCar
      }
      s"return $InvalidUpdateForUsedCar for an existing entry of used car" in new TestFixture {
        service.update(updateExistingUsedCar, existingUsedCar.carIdentification).futureValue shouldBe InvalidUpdateForUsedCar
      }
    }
    "delete" should {
      import com.scout24.cars.models.DeleteCarResult._
      s"return $DeleteSuccessful for an existing entry of car" in new TestFixture {
        service.delete(existingCar.carIdentification).futureValue shouldBe DeleteSuccessful
      }
    }
  }

  trait TestFixture {
    val daoStub = stub[CarsDao]

    val service = new CarServiceImpl(daoStub)

    List(existingCar, existingNonUsedCar, existingUsedCar) foreach
      (car => daoStub.read _ when car.carIdentification returns Future.successful(Some(car)))

    daoStub.read _ when nonExistingCar.carIdentification returns Future.successful(None)
    daoStub.create _ when nonExistingCar returns Future.successful(())
    daoStub.update _ when * returns Future.successful(())
    daoStub.delete _ when existingCar.carIdentification returns Future.successful(())
  }

}
