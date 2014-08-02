package com.hunorkovacs.koauthproxyfinagle

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._

object Proxy extends App {

  private val koauthFilter = new KoauthFilter()

  val client: Service[HttpRequest, HttpResponse] =
    koauthFilter andThen Http.newService("www.google.com:80")
    
  val server = Http.serve(":8080", client)
  Await.ready(server)
}
