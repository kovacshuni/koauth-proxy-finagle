package com.hunorkovacs.koauthproxyfinagle

import com.twitter.finagle.{Filter, Http, Service}
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._

import scala.concurrent.Future
import com.hunorkovacs.koauth.service.provider.ProviderService

trait SimpleFilter[Req, Rep] extends Filter[Req, Rep, Req, Rep]

class KoauthFilter(oauthService: ProviderService) extends SimpleFilter[HttpRequest, HttpResponse] {

  def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = {

    val eitherF = TheRequestMapper.map(request)
      .flatMap(oauthService.oauthenticate)

    val reee = eitherF flatMap { either =>
      val aa = either match {
        case Left(nok) => {
          val r = service(request)
          r
        }
        case Right(username) => {
          request.addHeader("x-koauth-proxy", username)
          val r = service(request)
          r
        }
      }

      aa
    }

    reee
  }
}

object Proxy extends App {
  val client: Service[HttpRequest, HttpResponse] =
    Http.newService("www.google.com:80")
    
  val server = Http.serve(":8080", client)
  Await.ready(server)








}
