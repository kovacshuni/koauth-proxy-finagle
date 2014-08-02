package com.hunorkovacs.koauthproxyfinagle

import com.hunorkovacs.koauth.service.provider.{ProviderServiceFactory, Persistence}
import com.hunorkovacs.koauthproxyfinagle.TheRequestMapper._
import com.hunorkovacs.koauthproxyfinagle.persistence.InMemoryPersistence
import com.twitter.finagle.{Service, Filter}
import com.twitter.util.{Promise, Future}
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}

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
