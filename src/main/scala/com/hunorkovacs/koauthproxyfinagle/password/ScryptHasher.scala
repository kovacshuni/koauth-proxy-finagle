package com.hunorkovacs.koauthproxyfinagle.password

import com.google.inject.Inject
import com.lambdaworks.crypto.SCryptUtil.{check, scrypt}

import scala.concurrent.{ExecutionContext, Future}

trait PasswordHasher {

  def verify(password: String, hash: String): Future[Boolean]

  def create(password: String): Future[String]
}

class ScryptHasher @Inject() (private val ec: ExecutionContext) extends PasswordHasher {

  private val N = 4096
  private val R = 8
  private val P = 1

  implicit private val implicitEc = ec

  override def verify(password: String, hash: String) =
    Future(check(password, hash))

  override def create(password: String) =
    Future(scrypt(password, N, R, P))
}
