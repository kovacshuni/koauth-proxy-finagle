package com.hunorkovacs.koauthproxyfinagle.persistence

import com.google.inject.Inject
import com.hunorkovacs.koauth.service.provider.persistence.Persistence
import com.hunorkovacs.koauthproxyfinagle.password.PasswordHasher

import scala.concurrent.Future.successful
import scala.concurrent.{Future, ExecutionContext}

class RouterProxyPersistence @Inject() (private val dynamoDbPers: DynamoDBPersistence,
                             private val cachedPers: AccessTokenCache,
                             private val redisPers: RedisPersistence,
                             private val passwordHasher: PasswordHasher,
                             private val ec: ExecutionContext) extends Persistence with ProxyPersistence {

  implicit private val implicitEc = ec

  override def nonceExists(nonce: String, consumerKey: String, token: String): Future[Boolean] =
    redisPers.nonceExists(nonce, consumerKey, token)

  override def persistNonce(nonce: String, consumerKey: String, token: String): Future[Unit] =
    redisPers.persistNonce(nonce, consumerKey, token)

  override def persistRequestToken(consumerKey: String, requestToken: String, requestTokenSecret: String,
                                   callback: String): Future[Unit] =
    dynamoDbPers.persistRequestToken(consumerKey, requestToken, requestTokenSecret, callback)

  override def getRequestTokenSecret(consumerKey: String, requestToken: String): Future[Option[String]] =
    dynamoDbPers.getRequestTokenSecret(consumerKey, requestToken)

  override def whoAuthorizedRequestToken(consumerKey: String, requestToken: String,
                                         verifier: String): Future[Option[String]] =
    dynamoDbPers.whoAuthorizedRequestToken(consumerKey, requestToken, verifier)

  override def authenticate(username: String, password: String): Future[Boolean] = {
    val expectedHashF = dynamoDbPers.getPassword(username)
    expectedHashF.flatMap {
      case None => successful(false)
      case Some(hash) => passwordHasher.verify(password, hash)
    }
  }

  override def authorizeRequestToken(consumerKey: String, requestToken: String, verifierUsername: String,
                                     verifier: String): Future[Unit] =
    dynamoDbPers.authorizeRequestToken(consumerKey, requestToken, verifierUsername, verifier)

  override def persistAccessToken(consumerKey: String, accessToken: String, accessTokenSecret: String,
                                  username: String): Future[Unit] =
    dynamoDbPers.persistAccessToken(consumerKey, accessToken, accessTokenSecret, username)

  override def getConsumerSecret(consumerKey: String): Future[Option[String]] =
    cachedPers.getConsumerSecret(consumerKey)

  override def getAccessTokenSecret(consumerKey: String, accessToken: String): Future[Option[String]] =
    cachedPers.getAccessTokenSecret(consumerKey, accessToken)

  override def getUsername(consumerKey: String, accessToken: String): Future[Option[String]] =
    cachedPers.getUsername(consumerKey, accessToken)
}
