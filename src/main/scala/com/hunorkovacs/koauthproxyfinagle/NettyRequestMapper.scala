package com.hunorkovacs.koauthproxyfinagle

import com.google.inject.Inject
import com.hunorkovacs.koauth.domain.KoauthRequest
import com.hunorkovacs.koauth.domain.mapper.RequestMapper
import org.jboss.netty.handler.codec.http.{QueryStringDecoder, HttpHeaders, HttpRequest}
import scala.collection.JavaConverters._

import scala.concurrent.{Future, ExecutionContext}

class NettyRequestMapper @Inject() (private val ec: ExecutionContext) extends RequestMapper[HttpRequest] {

  implicit private val implicitEc = ec

  override def map(source: HttpRequest): Future[KoauthRequest] = {
    Future {
      val method = source.getMethod.getName
      val queryStringDecoder = new QueryStringDecoder(source.getUri)
      val urlWithoutParams = "http://" + source.getHeader(HttpHeaders.Names.HOST) + queryStringDecoder.getPath
      val authHeader = Option(source.getHeader(HttpHeaders.Names.AUTHORIZATION)).getOrElse("")
      val urlParams = queryStringDecoder.getParameters.asScala.mapValues(_.get(0)).toList
      val bodyParams = List.empty

      KoauthRequest(method, urlWithoutParams, authHeader, urlParams, bodyParams)
    }
  }
}
