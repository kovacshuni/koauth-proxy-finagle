package com.hunorkovacs.koauthproxyfinagle

//#imports
import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import org.jboss.netty.handler.codec.http._
//#imports

object Server extends App {
//#service
  val service = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] =
      Future.value(new DefaultHttpResponse(
        req.getProtocolVersion, HttpResponseStatus.OK))
  }
//#service
//#builder
  val server = Http.serve(":8080", service)
  Await.ready(server)
//#builder
}
