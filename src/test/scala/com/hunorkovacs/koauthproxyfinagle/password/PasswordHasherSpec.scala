package com.hunorkovacs.koauthproxyfinagle.password

import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

class PasswordHasherSpec extends Specification {

  private val ec = ExecutionContext.Implicits.global
  private val hasher = new ScryptHasher(ec)

  "Verifying the hash of a password" should {
    "return true if hash is correct." in {
      hasher.verify("admin",
        "$s0$c0801$Hsrn3h6Q+4vE95oqJhi+iQ==$d3sxf7/9QPO5N8TxRgmvQ7znHIUaNUR/zGw921WShbM=") must
        beEqualTo(true).await
    }
    "return false if hash is incorrect." in {
      hasher.verify("admin",
        "$s0$c0801$Hsrn3h6Q+4vE95oqJhi+iQ==$123xf7/9QPO5N8TxRgmvQ7znHIUaNUR/zGw921WShbM=") must
        beEqualTo(false).await
    }
  }
}
