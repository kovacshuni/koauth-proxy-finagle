package com.hunorkovacs.koauthproxyfinagle

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.google.inject.AbstractModule
import com.hunorkovacs.koauth.service.provider.persistence.Persistence
import com.hunorkovacs.koauthproxyfinagle.persistence._
import net.codingwell.scalaguice.ScalaModule
import redis.clients.jedis.{JedisPoolConfig, JedisPool}

import scala.concurrent.ExecutionContext

class ProxyModule extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[ExecutionContext].toInstance(ExecutionContext.Implicits.global)
    bind[AmazonDynamoDBClient].toInstance(createDynamoDBClient)
    bind[JedisPool].toInstance(createJedisClient)
    bind[DynamoDBPersistence]
    bind[HardPersistence].to[DynamoDBPersistence]
    bind[AccessTokenCache]
    bind[RedisPersistence]
    bind[Persistence].to[RouterProxyPersistence]
    bind[KoauthFilter]
  }

  def createDynamoDBClient: AmazonDynamoDBClient = {
    val credentials = new ProfileCredentialsProvider().getCredentials
    val client1 = new AmazonDynamoDBClient(credentials)
    client1.setEndpoint("http://localhost:8000")
    client1
  }

  def createJedisClient: JedisPool =
    new JedisPool(new JedisPoolConfig(), "localhost")
}
