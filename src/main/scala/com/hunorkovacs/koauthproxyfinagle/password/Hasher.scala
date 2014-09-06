package com.hunorkovacs.koauthproxyfinagle.password

import com.lambdaworks.crypto.SCryptUtil
import com.lambdaworks.crypto.SCryptUtil.{check, scrypt}

import scala.concurrent.{ExecutionContext, Future}

trait PasswordHasher

class Hasher(private val ec: ExecutionContext) extends PasswordHasher {

  implicit private val implicitEc = ec

  def checke(password: String, hash: String) =
    Future(check(password, hash))
}
