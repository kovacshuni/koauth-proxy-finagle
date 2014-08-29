package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.provider.persistence.PersistenceSpec
import com.hunorkovacs.koauthproxyfinagle.Proxy.{createDynamoDBClient, createJedisClient}
import com.hunorkovacs.koauthproxyfinagle.persistence.DyanmoDBPersistenceSpec._

import scala.concurrent.ExecutionContext

class DyanmoDBPersistenceSpec extends PersistenceSpec(new RouterProxyPersistence(DynamoDB, Cache, Redis, Ec))

object DyanmoDBPersistenceSpec {
  val Ec = ExecutionContext.Implicits.global
  val DynamoDB = new DynamoDBPersistence(createDynamoDBClient, Ec)
  val Cache = new AccessTokenCache(DynamoDB, Ec)
  val Redis = new RedisPersistence(createJedisClient, Ec)
}
