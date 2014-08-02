package com.hunorkovacs.koauthproxyfinagle

import com.hunorkovacs.koauthproxyfinagle.TheRequestMapper.map
import com.twitter.finagle.{Filter, Http, Service}
import com.twitter.util.{Promise, Await, Future}
import org.jboss.netty.handler.codec.http._
import com.hunorkovacs.koauth.service.provider.{Persistence, ProviderServiceFactory}
import scala.concurrent.ExecutionContext

trait SimpleFilter[Req, Rep] extends Filter[Req, Rep, Req, Rep]

class KoauthFilter() extends SimpleFilter[HttpRequest, HttpResponse] {

  private implicit val ec = ExecutionContext.Implicits.global
  private implicit val persistence: Persistence = new InMemoryPersistence()
  private val oauthService = ProviderServiceFactory.createDefaultOauthService

  def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = {
    val eitherSF = map(request)
      .flatMap(oauthService.oauthenticate)
    val eitherF = scalaFuturetoTwitterFuture(eitherSF)

    eitherF flatMap {
      case Left(nok) =>
        service(request)
      case Right(username) =>
        request.addHeader("x-authorized", username)
        service(request)
    }
  }

  private def scalaFuturetoTwitterFuture[T](source: scala.concurrent.Future[T]): Future[T] = {
    val p = Promise[T]()
    source onSuccess {
      case success => p.setValue(success)
    }
    source onFailure {
      case failure: Throwable => p.setException(failure)
    }
    p
  }
}

object Proxy extends App {

  private val koauthFilter = new KoauthFilter()

  val client: Service[HttpRequest, HttpResponse] =
    koauthFilter andThen Http.newService("www.google.com:80")
    
  val server = Http.serve(":8080", client)
  Await.ready(server)
}
