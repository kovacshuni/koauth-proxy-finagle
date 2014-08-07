package com.hunorkovacs.koauthproxyfinagle

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.hunorkovacs.koauthproxyfinagle.persistence.DynamoDBPersistence
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._

import scala.concurrent.ExecutionContext

object Proxy extends App {

  private val hostname = if (args.length > 0) args(0) else "www.google.com:80"
  private val dbClient: AmazonDynamoDBClient = createClient
  private implicit val ec = ExecutionContext.Implicits.global
  private implicit val persistence = new DynamoDBPersistence(dbClient, ec)
  private val koauthFilter = new KoauthFilter(persistence)

  private val client: Service[HttpRequest, HttpResponse] =
    koauthFilter andThen Http.newService(hostname)
    
  private val server = Http.serve(":8080", client)

  Await.ready(server)

  def createClient = {
    val credentials = new ProfileCredentialsProvider().getCredentials
    val client1 = new AmazonDynamoDBClient(credentials)
    client1.setEndpoint("http://localhost:8000")
    client1
  }
}
