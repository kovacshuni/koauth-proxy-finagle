package com.hunorkovacs.koauthproxyfinagle

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.hunorkovacs.koauthproxyfinagle.persistence.{AccessTokenCache, RedisPersistence, RouterProxyPersistence, DynamoDBPersistence}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._
import redis.clients.jedis.{JedisPoolConfig, JedisPool, Jedis}

import scala.concurrent.ExecutionContext

object Proxy extends App {

  private val hostname = if (args.length > 0) args(0) else "www.google.com:80"
  private implicit val ec = ExecutionContext.Implicits.global
  private val dynamoDBPersistence = new DynamoDBPersistence(createDynamoDBClient, ec)
  private val dynamoDBCache = new AccessTokenCache(dynamoDBPersistence, ec)
  private val redisPersistence = new RedisPersistence(createJedisClient, ec)
  private implicit val routerProxyPersistence = new RouterProxyPersistence(dynamoDBPersistence, dynamoDBCache, redisPersistence, ec)
  private val koauthFilter = new KoauthFilter(routerProxyPersistence)

  private val client: Service[HttpRequest, HttpResponse] =
    koauthFilter andThen Http.newService(hostname)
    
  private val server = Http.serve(":8080", client)

  Await.ready(server)

  def createDynamoDBClient: AmazonDynamoDBClient = {
    val credentials = new ProfileCredentialsProvider().getCredentials
    val client1 = new AmazonDynamoDBClient(credentials)
    client1.setEndpoint("http://localhost:8000")
    client1
  }

  def createJedisClient: JedisPool =
    new JedisPool(new JedisPoolConfig(), "localhost")
}
