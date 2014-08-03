package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.provider.persistence.InMemoryPersistence
import scala.concurrent.ExecutionContext

class DyanmoDBPersistenceSpec extends PersistenceSpec(new DynamoDBPersistence(ExecutionContext.Implicits.global))

class InMemoryPersistenceSpec extends PersistenceSpec(new InMemoryPersistence()(ExecutionContext.Implicits.global))
