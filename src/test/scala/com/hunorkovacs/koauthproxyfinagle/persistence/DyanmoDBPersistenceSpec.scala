package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.provider.persistence.PersistenceSpec
import com.hunorkovacs.koauthproxyfinagle.ProxyModule.{createDynamoDBClient, createJedisClient}
import com.hunorkovacs.koauthproxyfinagle.password.ScryptHasher
import com.hunorkovacs.koauthproxyfinagle.persistence.DyanmoDBPersistenceSpec._

import scala.concurrent.ExecutionContext

class DyanmoDBPersistenceSpec extends PersistenceSpec(ProxyPersistence)

object DyanmoDBPersistenceSpec {
  private val Ec = ExecutionContext.Implicits.global
  private val DynamoDB = new DynamoDBPersistence(createDynamoDBClient, Ec)
  private val Cache = new AccessTokenCache(DynamoDB, Ec)
  private val Redis = new RedisPersistence(createJedisClient, Ec)
  private val Hasher = new ScryptHasher(Ec)
  val ProxyPersistence = new RouterProxyPersistence(DynamoDB, Cache, Redis, Hasher, Ec)
}
