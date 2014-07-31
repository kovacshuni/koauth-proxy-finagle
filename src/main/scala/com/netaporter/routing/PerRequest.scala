package com.netaporter.routing

import akka.actor._
import akka.actor.SupervisorStrategy.Stop
import spray.http.StatusCodes._
import spray.routing.RequestContext
import akka.actor.OneForOneStrategy
import spray.httpx.Json4sSupport
import scala.concurrent.duration._
import org.json4s.DefaultFormats
import spray.http.StatusCode
import com.netaporter._
import com.netaporter.routing.PerRequest._

trait PerRequest extends Actor with Json4sSupport {

  import context._

  val json4sFormats = DefaultFormats

  def requestContext: RequestContext
  def target: ActorRef
  def message: RestMessage

  setReceiveTimeout(2.seconds)
  target ! message

  def receive = {
    case result: RestMessage => complete(OK, result)
    case validation: Validation    => complete(BadRequest, validation)
    case ReceiveTimeout   => complete(GatewayTimeout, Error("Request timeout"))
  }

  def complete[T <: AnyRef](status: StatusCode, obj: T) = {
    requestContext.complete(status, obj)
    stop(self)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case e => {
        complete(InternalServerError, Error(e.getMessage))
        Stop
      }
    }
}

object PerRequest {
  case class WithActorRef(requestContext: RequestContext, target: ActorRef, message: RestMessage) extends PerRequest

  case class WithProps(requestContext: RequestContext, props: Props, message: RestMessage) extends PerRequest {
    lazy val target = context.actorOf(props)
  }
}

trait PerRequestCreator {
  this: Actor =>

  def perRequest(r: RequestContext, target: ActorRef, message: RestMessage) =
    context.actorOf(Props(new WithActorRef(r, target, message)))

  def perRequest(r: RequestContext, props: Props, message: RestMessage) =
    context.actorOf(Props(new WithProps(r, props, message)))
}
