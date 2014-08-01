package com.hunorkovacs.koauthproxyfinagle

import java.util.Date

import com.hunorkovacs.koauth.service.provider.Persistence

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class InMemoryPersistence extends Persistence {

  val consumers = ListBuffer[Consumer](
    Consumer("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "wr1KLYYH6o5yKFfiyN9ysKkPXcIAim2S", 0, "admin")
  )

  val requestTokens = ListBuffer[RequestToken]()

  val nonces = ListBuffer[Nonce]()

  override def nonceExists(nonce: String, consumerKey: String, token: String)
                          (implicit ec: ExecutionContext): Future[Boolean] = {
    Future {
      nonces.exists(p => nonce == p.nonce && consumerKey == p.consumerKey && token == p.token)
    }
  }

  override def authorizeRequestToken(consumerKey: String, requestToken: String, verifierUsername: String, verifier: String)
                                    (implicit ec: ExecutionContext): Future[Unit] = ???

  override def authenticate(username: String, password: String)
                           (implicit ec: ExecutionContext): Future[Boolean] = ???

  override def whoAuthorizedRequestToken(consumerKey: String, requestToken: String, verifier: String)
                                       (implicit ec: ExecutionContext): Future[Option[String]] = ???

  override def getAccessTokenSecret(consumerKey: String, accessToken: String)
                                   (implicit ec: ExecutionContext): Future[Option[String]] = ???

  override def persistAccessToken(consumerKey: String, accessToken: String, accessTokenSecret: String, username: String)
                                 (implicit ec: ExecutionContext): Future[Unit] = ???

  override def persistRequestToken(consumerKey: String, requestToken: String, requestTokenSecret: String, callback: String)
                                  (implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      requestTokens += RequestToken(consumerKey, requestToken, requestTokenSecret, callback, None, None)
      Unit
    }
  }

  override def getConsumerSecret(consumerKey: String)
                                (implicit ec: ExecutionContext): Future[Option[String]] = {
    Future {
      consumers.find(c => consumerKey == c.consumerKey)
        .map(c => c.consumerSecret)
    }
  }

  override def getUsername(consumerKey: String, accessToken: String)
                          (implicit ec: ExecutionContext): Future[String] = ???

  override def getRequestTokenSecret(consumerKey: String, requestToken: String)
                                    (implicit ec: ExecutionContext): Future[Option[String]] = ???

  override def persistNonce(nonce: String, consumerKey: String, token: String)
                           (implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      nonces += Nonce(nonce, new Date(), consumerKey, token)
      Unit
    }
  }
}
