package com.hunorkovacs.koauthproxyfinagle

import com.hunorkovacs.koauth.domain.KoauthResponse
import com.hunorkovacs.koauth.domain.mapper.ResponseMapper
import org.jboss.netty.handler.codec.http.HttpResponse

import scala.concurrent.{Future, ExecutionContext}

object TheResponseMapper extends ResponseMapper[HttpResponse] {

  override def map(source: KoauthResponse)
                  (implicit ec: ExecutionContext): Future[HttpResponse] = ???
}
