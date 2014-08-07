package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.provider.persistence.PersistenceSpec
import com.hunorkovacs.koauthproxyfinagle.Proxy.createClient

import scala.concurrent.ExecutionContext

class DyanmoDBPersistenceSpec extends PersistenceSpec(
  new DynamoDBPersistence(createClient, ExecutionContext.Implicits.global))
