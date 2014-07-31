package com.netaporter.clients

import akka.actor.Actor
import com.hunorkovacs.koauth.domain.KoauthRequest
import com.hunorkovacs.koauth.service.provider.ProviderServiceFactory
import com.netaporter._
import com.netaporter.clients.PetClient._
import com.netaporter.clients.OwnerClient._

/**
 * This could be:
 *  - a REST API we are the client for
 *  - a Database
 *  - anything else that requires IO
 */
class PetClient extends Actor {

  implicit val ec = context.dispatcher
  implicit val pers = new InMemoryPersistence()

  def receive = {
    case GetPets("Lion" :: _)     => {
      oauthService.requestToken(KoauthRequest("", "", "", List.empty, List.empty))
      sender ! Validation("Lions are too dangerous!")
    }

    case GetPets("Tortoise" :: _) => () // Never send a response. Tortoises are too slow
    case GetPets(petNames)        => sender ! Pets(petNames.map(Pet.apply))
  }
}
object PetClient {
  val oauthService = ProviderServiceFactory.createDefaultOauthService

  case class GetPets(petNames: List[String])
  case class Pets(pets: Seq[Pet])
}

/**
 * This could be:
 *  - a REST API we are the client for
 *  - a Database
 *  - anything else that requires IO
 */
class OwnerClient extends Actor {
  def receive = {
    case GetOwnersForPets(petNames) => {
      val owners = petNames map {
        case "Lassie"        => Owner("Jeff Morrow")
        case "Brian Griffin" => Owner("Peter Griffin")
        case "Tweety"        => Owner("Granny")
        case _               => Owner("Jeff") // Jeff has a lot of pets
      }
      sender ! OwnersForPets(owners)
    }
  }
}
object OwnerClient {
  case class GetOwnersForPets(petNames: Seq[String])
  case class OwnersForPets(owners: Seq[Owner])
}