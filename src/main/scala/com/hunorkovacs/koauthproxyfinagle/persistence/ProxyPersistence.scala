package com.hunorkovacs.koauthproxyfinagle.persistence

import scala.concurrent.Future

trait ProxyPersistence {

  def nonceExists(nonce: String,
                  consumerKey: String,
                  token: String): Future[Boolean]

  def persistNonce(nonce: String,
                   consumerKey: String,
                   token: String): Future[Unit]

  def persistRequestToken(consumerKey: String,
                          requestToken: String,
                          requestTokenSecret: String,
                          callback: String): Future[Unit]

  def getConsumerSecret(consumerKey: String): Future[Option[String]]

  def authorizeRequestToken(consumerKey: String,
                            requestToken: String,
                            verifierUsername: String,
                            verifier: String): Future[Unit]

  def authenticate(username: String, password: String): Future[Boolean]

  def whoAuthorizedRequestToken(consumerKey: String,
                                requestToken: String,
                                verifier: String): Future[Option[String]]

  def persistAccessToken(consumerKey: String,
                         accessToken: String,
                         accessTokenSecret: String,
                         username: String): Future[Unit]

  def getRequestTokenSecret(consumerKey: String, requestToken: String): Future[Option[String]]

  def getAccessTokenSecret(consumerKey: String, accessToken: String): Future[Option[String]]

  def getUsername(consumerKey: String, accessToken: String): Future[Option[String]]
}
