package com.netaporter.routing

import akka.actor.{Props, Actor}
import com.netaporter._
import spray.can.Http
import spray.http.HttpMethods._
import spray.http.{HttpResponse, Uri, HttpRequest}
import spray.routing.{Route, HttpService}
import com.netaporter.core.GetPetsWithOwnersActor
import com.netaporter.clients.{OwnerClient, PetClient}

class RestRouting extends HttpService with Actor with PerRequestCreator {

  implicit def actorRefFactory = context

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
//    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
//      sender() ! HttpResponse(entity = "PONG")
    case request: HttpRequest =>
      petsWithOwner {
        val names = request.uri.query.get("names").get
        GetPetsWithOwners(names.split(',').toList)
      }
  }

  val petService = context.actorOf(Props[PetClient])
  val ownerService = context.actorOf(Props[OwnerClient])

  /*
  val route = {
    get {
      path("pets") {
        parameters('names) { names =>
          petsWithOwner {
            GetPetsWithOwners(names.split(',').toList)
          }
        }
      }
    }
  }
  */

  def petsWithOwner(message : RestMessage): Route =
    ctx => perRequest(ctx, Props(new GetPetsWithOwnersActor(petService, ownerService)), message)
}
