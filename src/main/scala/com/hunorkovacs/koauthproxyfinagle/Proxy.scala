package com.hunorkovacs.koauthproxyfinagle

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._

//#app
object Proxy extends App {
  val client: Service[HttpRequest, HttpResponse] =
    Http.newService("www.google.com:80")
    
  val server = Http.serve(":8080", client)
  Await.ready(server)
}
//#app
