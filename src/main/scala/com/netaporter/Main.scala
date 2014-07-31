package com.netaporter

import akka.io.IO
import spray.can.Http

import akka.actor.{Props, ActorSystem}
import com.netaporter.routing.RestRouting

object Main extends App {
  implicit val system = ActorSystem("koauth-system")

  val serviceActor = system.actorOf(Props(new RestRouting), name = "rest-routing")

  system.registerOnTermination {
    system.log.info("Shutting down...")
  }

  IO(Http) ! Http.Bind(serviceActor, "localhost", port = 38080)
}
