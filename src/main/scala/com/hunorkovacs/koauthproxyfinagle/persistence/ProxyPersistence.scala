package com.hunorkovacs.koauthproxyfinagle.persistence

import scala.concurrent.{Future, ExecutionContext}

trait ProxyPersistence {

  def nonceExists(nonce: String,
                  consumerKey: String,
                  token: String)
                 (implicit ec: ExecutionContext): Future[Boolean]

  def persistNonce(nonce: String,
                   consumerKey: String,
                   token: String)
                  (implicit ec: ExecutionContext): Future[Unit]

  def persistRequestToken(consumerKey: String,
                          requestToken: String,
                          requestTokenSecret: String,
                          callback: String)
                         (implicit ec: ExecutionContext): Future[Unit]

  def getConsumerSecret(consumerKey: String)
                       (implicit ec: ExecutionContext): Future[Option[String]]

  def authorizeRequestToken(consumerKey: String,
                            requestToken: String,
                            verifierUsername: String,
                            verifier: String)
                           (implicit ec: ExecutionContext): Future[Unit]

  def authenticate(username: String, password: String)
                  (implicit ec: ExecutionContext): Future[Boolean]

  def whoAuthorizedRequestToken(consumerKey: String,
                                requestToken: String,
                                verifier: String)
                               (implicit ec: ExecutionContext): Future[Option[String]]

  def persistAccessToken(consumerKey: String,
                         accessToken: String,
                         accessTokenSecret: String,
                         username: String)
                        (implicit ec: ExecutionContext): Future[Unit]


  def getRequestTokenSecret(consumerKey: String, requestToken: String)
                           (implicit ec: ExecutionContext): Future[Option[String]]

  def getAccessTokenSecret(consumerKey: String, accessToken: String)
                          (implicit ec: ExecutionContext): Future[Option[String]]

  def getUsername(consumerKey: String, accessToken: String)
                 (implicit ec: ExecutionContext): Future[Option[String]]

}
