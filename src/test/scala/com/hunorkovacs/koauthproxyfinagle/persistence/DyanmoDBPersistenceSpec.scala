package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.provider.persistence.PersistenceSpec
import com.hunorkovacs.koauthproxyfinagle.Proxy.{createDynamoDBClient, createJedisClient}

import scala.concurrent.ExecutionContext

class DyanmoDBPersistenceSpec extends PersistenceSpec(
  new RouterProxyPersistence(new DynamoDBPersistence(createDynamoDBClient, ExecutionContext.Implicits.global),
    new RedisPersistence(createJedisClient, ExecutionContext.Implicits.global),
    ExecutionContext.Implicits.global))
