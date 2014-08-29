package com.hunorkovacs.koauthproxyfinagle.persistence

import spray.caching.LruCache

import scala.concurrent.duration._
import scala.concurrent.{Future, ExecutionContext}

class AccessTokenCache(private val hardPersistence: HardPersistence,
                       private val ec: ExecutionContext) {

  val consumerSecretCache = LruCache[Option[String]](maxCapacity = 10000,
    initialCapacity = 50,
    timeToLive = Duration.Inf,
    timeToIdle = Duration.Inf)
  val accessTokenSecretCache = LruCache[Option[String]](maxCapacity = 10000,
    initialCapacity = 100,
    timeToLive = Duration.Inf,
    timeToIdle = 15 minutes)
  val usernameCache = LruCache[Option[String]](maxCapacity = 10000,
    initialCapacity = 100,
    timeToLive = Duration.Inf,
    timeToIdle = 15 minutes)

  def getConsumerSecret(consumerKey: String)
                       (implicit ec: ExecutionContext): Future[Option[String]] =
    consumerSecretCache(consumerKey, () => hardPersistence.getConsumerSecret(consumerKey))

  def getAccessTokenSecret(consumerKey: String, accessToken: String)
                          (implicit ec: ExecutionContext): Future[Option[String]] = {
    val key = StringBuilder.newBuilder.append(consumerKey).append(accessToken).mkString
    accessTokenSecretCache(key, () => hardPersistence.getAccessTokenSecret(consumerKey, accessToken))
  }

  def getUsername(consumerKey: String, accessToken: String)
                 (implicit ec: ExecutionContext): Future[Option[String]] = {
    val key = StringBuilder.newBuilder.append(consumerKey).append(accessToken).mkString
    usernameCache(key, () => hardPersistence.getUsername(consumerKey, accessToken))
  }
}
