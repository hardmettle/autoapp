package com.scout24.cars.http

import akka.http.scaladsl.server.Directives.{ handleRejections, pathPrefix }
import com.scout24.cars.http.routes.CarRoutes

class HttpService(carRoutes: CarRoutes) extends RejectionHandling {
  val routes =
    handleRejections(customRejectionHandler) {
      pathPrefix("v1") {
        carRoutes.carRoutes
      }
    }

}

object HttpService {
  def apply(): HttpService =
    new HttpService(CarRoutes())
}

