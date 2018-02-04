package com.scout24.cars

import com.scout24.cars.services.dao.CarsDao
import com.scout24.cars.utils.Migration
import org.scalatest.BeforeAndAfterAll

trait IntegrationSpec extends BaseSpec with Migration with BeforeAndAfterAll {
  val dao = CarsDao()

  override protected def beforeAll() = {
    reloadSchema()
  }

  override protected def afterAll() = {
    reloadSchema()
  }

}
