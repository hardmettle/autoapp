package com.scout24.cars.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Validation {
  import java.text.SimpleDateFormat

  def isValidFormat(format: String = "dd/MM/yyyy", value: String): Boolean = {

    try {
      val sdf = new SimpleDateFormat(format)
      sdf.setLenient(false)
      val date = sdf.parse(value)
      val currentDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
      if (value == sdf.format(date) && date.before(sdf.parse(currentDt))) true else false
    } catch {
      case ex: Exception =>
        false
    }
  }
}
