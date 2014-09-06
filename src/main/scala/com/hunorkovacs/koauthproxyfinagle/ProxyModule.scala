package com.hunorkovacs.koauthproxyfinagle

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.google.inject.{Provides, AbstractModule}
import com.hunorkovacs.koauth.service.provider.{ProviderServiceFactory, ProviderService}
import com.hunorkovacs.koauth.service.provider.persistence.Persistence
import com.hunorkovacs.koauthproxyfinagle.password.{ScryptHasher, PasswordHasher}
import com.hunorkovacs.koauthproxyfinagle.persistence._
import net.codingwell.scalaguice.ScalaModule
import redis.clients.jedis.{JedisPoolConfig, JedisPool}

import scala.concurrent.ExecutionContext

class ProxyModule extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[ExecutionContext].toInstance(ExecutionContext.Implicits.global)
    bind[DynamoDBPersistence]
    bind[HardPersistence].to[DynamoDBPersistence]
    bind[AccessTokenCache]
    bind[Persistence].to[RouterProxyPersistence]
    bind[PasswordHasher].to[ScryptHasher]
    bind[NettyRequestMapper]
    bind[KoauthFilter]
  }

  @Provides
  private def createDynamoDBClient: AmazonDynamoDBClient =
    ProxyModule.createDynamoDBClient

  @Provides
  private def createJedisClient: JedisPool =
    ProxyModule.createJedisClient

  @Provides
  private def createProviderService(persistence: Persistence, ec: ExecutionContext) =
    ProxyModule.createProviderService(persistence, ec)
}

object ProxyModule {

  def createDynamoDBClient: AmazonDynamoDBClient = {
    val credentials = new ProfileCredentialsProvider().getCredentials
    val client1 = new AmazonDynamoDBClient(credentials)
    client1.setEndpoint("http://localhost:8000")
    client1
  }

  def createJedisClient: JedisPool =
    new JedisPool(new JedisPoolConfig(), "localhost")

  def createProviderService(persistence: Persistence, ec: ExecutionContext) =
    ProviderServiceFactory.createProviderService(persistence, ec)
}