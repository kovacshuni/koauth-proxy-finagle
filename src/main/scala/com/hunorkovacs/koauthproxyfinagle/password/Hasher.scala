package com.hunorkovacs.koauthproxyfinagle.password

import com.lambdaworks.crypto.SCryptUtil.{check, scrypt}

import scala.concurrent.{ExecutionContext, Future}

trait PasswordHasher

class Hasher(private val ec: ExecutionContext) extends PasswordHasher {

  private val N = 4096
  private val R = 8
  private val P = 1

  implicit private val implicitEc = ec

  def verify(password: String, hash: String) =
    Future(check(password, hash))

  def create(password: String) =
    Future(scrypt(password, N, R, P))
}
