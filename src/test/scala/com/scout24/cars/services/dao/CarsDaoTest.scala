package com.scout24.cars.services.dao

import java.sql.SQLException

import com.scout24.cars.IntegrationSpec
import com.scout24.cars.models.CarUpdate
import org.scalatest.BeforeAndAfterEach

class CarsDaoTest extends IntegrationSpec with BeforeAndAfterEach {
  "create" should {

    "insert a new entry" in {
      val info = carInfo()
      dao.read(info.carIdentification).futureValue shouldBe None
      dao.create(info).futureValue shouldBe ((): Unit)
      dao.read(info.carIdentification).futureValue.value shouldBe info
    }

    "throw an exception on existing entry insertion" in {
      val info = carInfo()
      dao.create(info).futureValue
      whenReady(dao.create(info).failed) { e =>
        e shouldBe a[SQLException]
      }
    }

  }

  "read" should {

    "return an existing entry" in {
      val info = carInfo()
      dao.create(info).futureValue
      dao.read(info.carIdentification).futureValue.value shouldBe info
    }

    "return none for non existing entry" in {
      val info = carInfo()
      dao.read(info.carIdentification).futureValue shouldBe None
    }

  }
  "update" should {

    "update an existing entry of car" in {
      val info = carInfo().copy(price = 100)
      dao.create(info).futureValue

      def dbInfo = dao.read(info.carIdentification).futureValue.value

      dbInfo.price shouldBe 100
      dao.update(info.copy(price = 200)).futureValue
      dbInfo.price shouldBe 200
    }

    "do nothing for non existing entry" in {
      dao.update(carInfo()).futureValue shouldBe ((): Unit)
    }

  }

}
