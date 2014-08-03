package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.provider.persistence.PersistenceSpec

import scala.concurrent.ExecutionContext

class DyanmoDBPersistenceSpec extends PersistenceSpec(new DynamoDBPersistence(ExecutionContext.Implicits.global))
