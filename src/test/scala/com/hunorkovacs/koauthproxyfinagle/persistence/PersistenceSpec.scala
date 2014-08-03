package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.Generator.{generateNonce, generateTokenAndSecret}
import com.hunorkovacs.koauth.service.provider.persistence.Persistence
import scala.concurrent.Await.ready
import scala.concurrent.duration._
import scala.concurrent.Await
import org.specs2.mutable._

abstract class PersistenceSpec(val pers: Persistence) extends Specification {

  "Querying if Nonce exists" should {
    "return true if it was persisted before." in {
      val (nonce, consumerKey, token) = generateForNonce

      ready(pers.persistNonce(nonce, consumerKey, token), 1.0 second)

      pers.nonceExists(nonce, consumerKey, token) must beEqualTo(true).await
    }
    "return false if it was not persisted before." in {
      val (nonce, consumerKey, token) = generateForNonce

      Await.ready(pers.persistNonce(nonce, consumerKey, token), 1.0 second)

      pers.nonceExists(generateNonce, consumerKey, token) must beEqualTo(false).await
    }
  }

  private def generateForNonce =
    (generateNonce, generateTokenAndSecret._1, generateTokenAndSecret._1)
}
