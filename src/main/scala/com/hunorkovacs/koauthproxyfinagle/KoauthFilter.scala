package com.hunorkovacs.koauthproxyfinagle

import com.hunorkovacs.koauth.domain.{ResponseBadRequest, ResponseUnauthorized}
import com.hunorkovacs.koauth.service.provider.persistence.{Persistence, ExampleMemoryPersistence}
import com.hunorkovacs.koauth.service.provider.ProviderServiceFactory
import com.hunorkovacs.koauthproxyfinagle.persistence.DynamoDBPersistence
import com.twitter.finagle.{Service, Filter}
import com.twitter.util.{Promise, Future}
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import org.jboss.netty.handler.codec.http.HttpResponseStatus.{UNAUTHORIZED, BAD_REQUEST}

import scala.concurrent.ExecutionContext

trait SimpleFilter[Req, Rep] extends Filter[Req, Rep, Req, Rep]

class KoauthFilter() extends SimpleFilter[HttpRequest, HttpResponse] {

  private val HeaderAuthenticated = "x-authenticated"
  private val HeaderAuthenticatedMethod = "x-authentication-method"
  private val HeaderOauth1 = "oauth1"
  private val HeaderUnauthenticatedCode = "x-unauthenticated-code"
  private val HeaderUnauthenticatedMessage = "x-unauthenticated-message"
  private val Unauthorized = UNAUTHORIZED.getCode
  private val BadRequest = BAD_REQUEST.getCode

  private implicit val ec = ExecutionContext.Implicits.global
  private implicit val persistence: Persistence = new DynamoDBPersistence(ec)
  private val oauthService = ProviderServiceFactory.createDefaultOauthService

  def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = {
    val eitherSF = NettyRequestMapper.map(request)
      .flatMap(oauthService.oauthenticate)

    scalaFuturetoTwitterFuture(eitherSF) flatMap {
      case Left(nok) =>
        val filteredRequest = nok match {
          case ResponseUnauthorized(message) =>
            request.addHeader(HeaderUnauthenticatedCode, Unauthorized)
            request.addHeader(HeaderUnauthenticatedMessage, message)
            request
          case ResponseBadRequest(message) =>
            request.addHeader(HeaderUnauthenticatedCode, BadRequest)
            request.addHeader(HeaderUnauthenticatedMessage, message)
            request
        }
        service(filteredRequest)
      case Right(username) =>
        request.addHeader(HeaderAuthenticated, username)
        request.addHeader(HeaderAuthenticatedMethod, HeaderOauth1)
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
