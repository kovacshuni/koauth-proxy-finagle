package com.hunorkovacs.koauthproxyfinagle

import com.hunorkovacs.koauth.service.provider.ProviderServiceFactory
import com.hunorkovacs.koauth.service.provider.persistence.ExampleMemoryPersistence
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._

import scala.concurrent.ExecutionContext

object Proxy extends App {

  private val hostname = if (args.length > 0) args(0) else "www.google.com:80"
  private implicit val ec = ExecutionContext.Implicits.global
  private val mapper = new NettyRequestMapper(ec)
  private val persistence = new ExampleMemoryPersistence(ec)
  private val oauthService = ProviderServiceFactory.createProviderService(persistence, ec)
  private val koauthFilter = new KoauthFilter(mapper, oauthService, persistence, ec)
  private val client: Service[HttpRequest, HttpResponse] =
    koauthFilter andThen Http.newService(hostname)
  private val server = Http.serve(":8080", client)

  Await.ready(server)
}
