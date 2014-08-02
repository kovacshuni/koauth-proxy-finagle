package com.hunorkovacs.koauthproxyfinagle

import com.hunorkovacs.koauth.domain.{ResponseBadRequest, ResponseUnauthorized, KoauthResponse}
import com.hunorkovacs.koauth.domain.mapper.ResponseMapper
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http.HttpResponseStatus.{UNAUTHORIZED, BAD_REQUEST}
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.handler.codec.http.{HttpResponseStatus, DefaultHttpResponse, HttpResponse}
import org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import org.jboss.netty.util.CharsetUtil.UTF_8

import scala.concurrent.{Future, ExecutionContext}

object NettyResponseMapper extends ResponseMapper[HttpResponse] {

  override def map(source: KoauthResponse)
                  (implicit ec: ExecutionContext): Future[HttpResponse] = {
    Future {
      source match {
        case ResponseUnauthorized(message) => simpleResponse(UNAUTHORIZED, message)
        case ResponseBadRequest(message) => simpleResponse(BAD_REQUEST, message)
        case _ => ???
      }
    }
  }

  private def simpleResponse(status: HttpResponseStatus, message: String) = {
    val response = new DefaultHttpResponse(HTTP_1_1, status)
    response.setContent(ChannelBuffers.copiedBuffer(message, UTF_8))
    response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8")
    response
  }
}
