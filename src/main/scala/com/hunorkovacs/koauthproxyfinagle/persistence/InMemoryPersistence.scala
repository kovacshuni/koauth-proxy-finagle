package com.hunorkovacs.koauthproxyfinagle.persistence

import java.util.Date

import com.hunorkovacs.koauth.service.provider.Persistence

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class InMemoryPersistence(implicit val ec: ExecutionContext) extends Persistence {

  val consumers = ListBuffer[Consumer](
    Consumer("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "wr1KLYYH6o5yKFfiyN9ysKkPXcIAim2S", 0, "admin")
  )

  val requestTokens = ListBuffer[RequestToken](
    RequestToken("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "nHmH9Qv6vPhZuvLVfofIXoKqpKA6BcSq",
      "S6o9gbm6l6yyR3kcry9kzj40C6mhErmu", "oob", None, None),
    RequestToken("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "DGnMlgdnCxc5ur3ZYX5t1BSjUOJUyqfZ",
      "y6v2ZtztCLH9Yewoeb4NoIXRmWlb74xV", "oob", Some("admin"), Some("W8FMcCtnDZ1Gw1m4"))
  )

  val accessTokens = ListBuffer[AccessToken](
    AccessToken("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "NDW4H8pFTthDV7kmSkdyYDmiBspabYEW",
      "e3lqNSPq1hU6v7FFnq6p6die6pFIYJU0", "admin")
  )

  val nonces = ListBuffer[Nonce]()

  val users = ListBuffer[User](
    User("admin", "admin")
  )

  override def nonceExists(nonce: String, consumerKey: String, token: String)
                          (implicit ec: ExecutionContext): Future[Boolean] = {
    Future {
      nonces.exists(p => nonce == p.nonce && consumerKey == p.consumerKey && token == p.token)
    }
  }

  override def authorizeRequestToken(consumerKey: String, requestToken: String, verifierUsername: String, verifier: String)
                                    (implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      val searchedRequestToken =
        requestTokens.find(p => consumerKey == p.consumerKey && requestToken == p.requestToken).get
      val verifiedToken = RequestToken(consumerKey, requestToken, searchedRequestToken.requestTokenSecret,
        searchedRequestToken.callback, Some(verifierUsername), Some(verifier))
      requestTokens.update(requestTokens.indexOf(searchedRequestToken), verifiedToken)
    }
  }

  override def authenticate(username: String, password: String)
                           (implicit ec: ExecutionContext): Future[Boolean] = {
    Future {
      users.find(u => username == u.username && password == u.password) match {
        case None => false
        case Some(user) => true
      }
    }
  }

  override def whoAuthorizedRequestToken(consumerKey: String, requestToken: String, verifier: String)
                                       (implicit ec: ExecutionContext): Future[Option[String]] = {
    Future {
      requestTokens.find(p => consumerKey == p.consumerKey && requestToken == p.requestToken) match {
        case None => None
        case Some(foundRequestToken) => foundRequestToken.verifierUsername
      }
    }
  }

  override def getAccessTokenSecret(consumerKey: String, accessToken: String)
                                   (implicit ec: ExecutionContext): Future[Option[String]] = {
    Future {
      accessTokens.find(t => consumerKey == t.consumerKey && accessToken == t.accessToken)
        .map(t => t.accessTokenSecret)
    }
  }

  override def persistAccessToken(consumerKey: String, accessToken: String, accessTokenSecret: String, username: String)
                                 (implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      accessTokens += AccessToken(consumerKey, accessToken, accessTokenSecret, username)
      Unit
    }
  }

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
                          (implicit ec: ExecutionContext): Future[String] = {
    Future {
      accessTokens.find(t => consumerKey == t.consumerKey && accessToken == t.accessToken).get.username
    }
  }

  override def getRequestTokenSecret(consumerKey: String, requestToken: String)
                                    (implicit ec: ExecutionContext): Future[Option[String]] = {
    Future {
      requestTokens.find(t => consumerKey == t.consumerKey && requestToken == t.requestToken)
        .map(t => t.requestTokenSecret)
    }
  }

  override def persistNonce(nonce: String, consumerKey: String, token: String)
                           (implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      nonces += Nonce(nonce, new Date(), consumerKey, token)
      Unit
    }
  }
}
