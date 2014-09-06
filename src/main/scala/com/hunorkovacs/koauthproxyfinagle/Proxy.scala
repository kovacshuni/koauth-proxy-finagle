package com.hunorkovacs.koauthproxyfinagle

import com.google.inject.Guice.createInjector
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._

import scala.concurrent.ExecutionContext

object Proxy extends App {

  private val hostname = if (args.length > 0) args(0) else "www.google.com:80"

  val injector = createInjector(new ProxyModule())
  import net.codingwell.scalaguice.InjectorExtensions._

  private implicit val ec = injector.instance[ExecutionContext]
  private val koauthFilter = injector.instance[KoauthFilter]

  private val client: Service[HttpRequest, HttpResponse] =
    koauthFilter andThen Http.newService(hostname)
    
  private val server = Http.serve(":8080", client)

  Await.ready(server)
}
