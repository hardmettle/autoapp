package com.scout24.cars

import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.Http
import com.scout24.cars.http.HttpService
import com.scout24.cars.utils.{ ActorContext, Config, Migration }

object Main extends App with Config with Migration with ActorContext {
  val log: LoggingAdapter = Logging(system, getClass)
  val httpService = HttpService()

  //reloadSchema()
  migrate()

  Http().bindAndHandle(httpService.routes, httpInterface, httpPort)
}
