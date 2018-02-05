package com.scout24.cars.utils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

//Instantiate system actor with ExecutionContext and ActorMaterializer
trait ActorContext {
  implicit val system = ActorSystem("auto-system")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
}
