package com.scout24.cars.http

import akka.http.scaladsl.server.Directives.{ handleRejections, pathPrefix }
import com.scout24.cars.http.routes.CarRoutes

//Http service layer that takes CarRoutes and also rejection handler to handle rejection of request
class HttpService(carRoutes: CarRoutes) extends RejectionHandling {
  val routes =
    handleRejections(customRejectionHandler) {
      pathPrefix("v1") {
        carRoutes.carRoutes
      }
    }

}
//Companion object to create HttpService
object HttpService {
  def apply(): HttpService =
    new HttpService(CarRoutes())
}

