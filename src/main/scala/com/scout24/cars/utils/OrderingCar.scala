package com.scout24.cars.utils

import java.text.SimpleDateFormat

import com.scout24.cars.models.CarRegistration

//Utility to order car using registration date
object OrderingCar {
  def orderCarWithRegistrationDate(car1: CarRegistration, car2: CarRegistration): Boolean = {
    (car1.registration, car2.registration) match {
      case (Some(r1), Some(r2)) =>
        val sdf = new SimpleDateFormat("dd/MM/yyyy")
        sdf.setLenient(false)
        val date1 = sdf.parse(r1)
        val date2 = sdf.parse(r2)
        date1.before(date2)
      case (None, Some(r2)) => true
      case (Some(r2), None) => false
      case (None, None) => car1.id > car2.id
    }
  }
}
