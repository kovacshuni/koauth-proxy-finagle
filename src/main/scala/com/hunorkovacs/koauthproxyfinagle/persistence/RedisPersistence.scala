package com.hunorkovacs.koauthproxyfinagle.persistence

import redis.clients.jedis.{JedisPool, Jedis}

import scala.concurrent.{Future, ExecutionContext}

class RedisPersistence(private val jedisPool: JedisPool, private val ec: ExecutionContext) {

  private val Dummy = ""

  def nonceExists(nonce: String, consumerKey: String, token: String)
                          (implicit ec: ExecutionContext): Future[Boolean] = {
    Future {
      val jedis = jedisPool.getResource
      val id = createNonceId(nonce, consumerKey, token)
      jedis.exists(id)
    }
  }

  def persistNonce(nonce: String, consumerKey: String, token: String)
                           (implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      val jedis = jedisPool.getResource
      val id = createNonceId(nonce, consumerKey, token)
      jedis.set(id, Dummy)
      Unit
    }
  }

  private def createNonceId(nonce: String, consumerKey: String, token: String) =
    new StringBuilder(nonce).append(consumerKey).append(token).mkString
}
