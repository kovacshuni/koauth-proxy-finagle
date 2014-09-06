package com.hunorkovacs.koauthproxyfinagle.password

import com.lambdaworks.crypto.SCryptUtil
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

class PasswordHasherSpec extends Specification {

  private val ec = ExecutionContext.Implicits.global
//  private val hasher = new Hasher(ec)

  "password hashing" should {
    "give correct hash" in {
      println(System.getProperty("com.lambdaworks.jni.loader"))
      println()

//      val hasher = new Hasher(ec)
//      hasher.checke("admin", "123") must beEqualTo(true).await

      var hash = ""
      var sum = 0l
      val n = 100
      for (i <- 1 to n) {
        val timeA = System.currentTimeMillis()
        hash = SCryptUtil.scrypt("admin", 2, 1, 1)
        val timeB = System.currentTimeMillis()
        sum = sum + timeB - timeA
      }
      println("%1.0f" format sum.toFloat / n)
      println()

      println(hash)
      println()

      1 must beEqualTo(1)
    }
  }
}
