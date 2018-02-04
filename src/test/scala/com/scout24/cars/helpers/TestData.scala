package com.scout24.cars.helpers

import java.time.LocalDate
import java.util.Calendar

import com.scout24.cars.models.{CarRegistration, Fuel}

import scala.util.Random

trait TestData {
  def carInfo(
               id: Long = randomLong() & Long.MaxValue,
               title: String = randomString(),
               fuel: Fuel = Fuel(name = randomString(), renewable = randomBoolean()),
               price: Int = randomInt() & Integer.MAX_VALUE,
               used: Boolean = randomBoolean(),
               mileage:Int = randomInt() & Integer.MAX_VALUE,
               registration:String = randomDate()
             ): CarRegistration =
    CarRegistration(
      id = id,
      title = title,
      fuel = fuel,
      price = price,
      used = used,
      mileage = if(used) Some(mileage) else None,
      registration = if(used) Some(registration) else None,
    )

  def randomLong(): Long = Random.nextLong()

  def randomInt(): Int = Random.nextInt()

  def randomBoolean(): Boolean = Random.nextBoolean()

  def randomString(): String = Random.alphanumeric.take(10).mkString("")

  def randomDate():String = {
    import java.time.format.DateTimeFormatter
    import java.time.temporal.ChronoUnit
    import java.util.concurrent.ThreadLocalRandom
    val from = LocalDate.of(2016, 1, 1)
    val calendarInstance = Calendar.getInstance()
    val to = LocalDate.of(calendarInstance.get(Calendar.YEAR),
      calendarInstance.get(Calendar.MONTH) + 1, calendarInstance.get(Calendar.DAY_OF_MONTH))
    val days = from.until(to, ChronoUnit.DAYS)
    val randomDays = ThreadLocalRandom.current.nextLong(days)
    val randomDate = from.plusDays(randomDays)
    randomDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
  }
}
