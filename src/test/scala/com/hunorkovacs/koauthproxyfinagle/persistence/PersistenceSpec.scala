package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.Generator.{generateNonce, generateTokenAndSecret}
import com.hunorkovacs.koauth.service.provider.persistence.Persistence
import scala.concurrent.blocking
import org.specs2.mutable._

abstract class PersistenceSpec(val pers: Persistence) extends Specification {

  "Querying if Nonce exists" should {
    "return true if it was persisted before." in {
      val (nonce, consumerKey, token) = generateForNonce

      pers.persistNonce(nonce, consumerKey, token)

      blocking(Thread.sleep(2000))
      pers.nonceExists(nonce, consumerKey, token) must beEqualTo(true).await
    }
    "return false if it was not persisted before." in {
      val (nonce, consumerKey, token) = generateForNonce

      pers.persistNonce(nonce, consumerKey, token)

      blocking(Thread.sleep(2000))
      pers.nonceExists(generateNonce, consumerKey, token) must beEqualTo(false).await
    }
  }

  private def generateForNonce =
    (generateNonce, generateTokenAndSecret._1, generateTokenAndSecret._1)
}
