package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.provider.persistence.Persistence

import scala.concurrent.{Future, ExecutionContext}

class RouterProxyPersistence(private val dynamoDbPers: DynamoDBPersistence,
                             private val redisPers: RedisPersistence,
                             private val ec: ExecutionContext) extends Persistence with ProxyPersistence {

  override def nonceExists(nonce: String, consumerKey: String, token: String)
                          (implicit ec: ExecutionContext): Future[Boolean] =
    redisPers.nonceExists(nonce, consumerKey, token)

  override def persistNonce(nonce: String, consumerKey: String, token: String)
                           (implicit ec: ExecutionContext): Future[Unit] =
    redisPers.persistNonce(nonce, consumerKey, token)

  override def authorizeRequestToken(consumerKey: String, requestToken: String, verifierUsername: String, verifier: String)
                                    (implicit ec: ExecutionContext): Future[Unit] =
    dynamoDbPers.authorizeRequestToken(consumerKey, requestToken, verifierUsername, verifier)

  override def authenticate(username: String, password: String)
                           (implicit ec: ExecutionContext): Future[Boolean] =
    dynamoDbPers.authenticate(username, password)

  override def getAccessTokenSecret(consumerKey: String, accessToken: String)
                                   (implicit ec: ExecutionContext): Future[Option[String]] =
    dynamoDbPers.getAccessTokenSecret(consumerKey, accessToken)

  override def persistAccessToken(consumerKey: String, accessToken: String, accessTokenSecret: String, username: String)
                                 (implicit ec: ExecutionContext): Future[Unit] =
    dynamoDbPers.persistAccessToken(consumerKey, accessToken, accessTokenSecret, username)

  override def whoAuthorizedRequestToken(consumerKey: String, requestToken: String, verifier: String)
                                        (implicit ec: ExecutionContext): Future[Option[String]] =
    dynamoDbPers.whoAuthorizedRequestToken(consumerKey, requestToken, verifier)

  override def persistRequestToken(consumerKey: String, requestToken: String, requestTokenSecret: String, callback: String)
                                  (implicit ec: ExecutionContext): Future[Unit] =
    dynamoDbPers.persistRequestToken(consumerKey, requestToken, requestTokenSecret, callback)

  override def getConsumerSecret(consumerKey: String)
                                (implicit ec: ExecutionContext): Future[Option[String]] =
    dynamoDbPers.getConsumerSecret(consumerKey)

  override def getUsername(consumerKey: String, accessToken: String)
                          (implicit ec: ExecutionContext): Future[Option[String]] =
    dynamoDbPers.getUsername(consumerKey, accessToken)

  override def getRequestTokenSecret(consumerKey: String, requestToken: String)
                                    (implicit ec: ExecutionContext): Future[Option[String]] =
    dynamoDbPers.getRequestTokenSecret(consumerKey, requestToken)
}
