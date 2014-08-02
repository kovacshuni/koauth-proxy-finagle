package com.hunorkovacs.koauthproxyfinagle

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._

object Proxy extends App {

  private val hostname =
    if (args.length > 0) args(0)
    else "www.google.com:80"
  private val koauthFilter = new KoauthFilter()

  val client: Service[HttpRequest, HttpResponse] =
    koauthFilter andThen Http.newService(hostname)
    
  val server = Http.serve(":8080", client)
  Await.ready(server)
}
