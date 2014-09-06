package com.hunorkovacs.koauthproxyfinagle.persistence

import com.hunorkovacs.koauth.service.DefaultTokenGenerator.generateTokenAndSecret
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.concurrent.Future.successful
import scala.concurrent.{Future, ExecutionContext}

class AccessTokenCacheSpec extends Specification with Mockito {

  private val ec = ExecutionContext.Implicits.global

  "Retrieving Consumer Secret" should {
    "go only once to the underlying DB, cache the value and return from cache onwards." in {
      val hardPersistence = mock[HardPersistence]
      val cache = new AccessTokenCache(hardPersistence, ec)
      val (consumerKey, expectedConsumerSecret) = generateTokenAndSecret
      hardPersistence.getConsumerSecret(consumerKey)returns successful(Some(expectedConsumerSecret))

      var actualConsumerSecret = Future[Option[String]](None)
      for (_ <- 1 to 10)
        actualConsumerSecret = cache.getConsumerSecret(consumerKey)

      actualConsumerSecret must beEqualTo(Some(expectedConsumerSecret)).await and {
        there was one(hardPersistence).getConsumerSecret(consumerKey)
      }
    }
  }

  "Retrieving Access Token Secret" should {
    "go only once to the underlying DB, cache the value and return from cache onwards." in {
      val hardPersistence = mock[HardPersistence]
      val cache = new AccessTokenCache(hardPersistence, ec)
      val consumerKey = generateTokenAndSecret._1
      val (accessToken, expectedTokenSecret) = generateTokenAndSecret
      hardPersistence.getAccessTokenSecret(consumerKey, accessToken) returns successful(Some(expectedTokenSecret))

      var actualTokenSecret = Future[Option[String]](None)
      for (_ <- 1 to 10)
        actualTokenSecret = cache.getAccessTokenSecret(consumerKey, accessToken)

      actualTokenSecret must beEqualTo(Some(expectedTokenSecret)).await and {
        there was one(hardPersistence).getAccessTokenSecret(consumerKey, accessToken)
      }
    }
  }

  "Retrieving Username associated to Access Token" should {
    "go only once to the underlying DB, cache the value and return from cache onwards." in {
      val hardPersistence = mock[HardPersistence]
      val cache = new AccessTokenCache(hardPersistence, ec)
      val consumerKey = generateTokenAndSecret._1
      val (accessToken, _) = generateTokenAndSecret
      val expectedUsername = "username123"
      hardPersistence.getUsername(consumerKey, accessToken) returns successful(Some(expectedUsername))

      var actualUsername = Future[Option[String]](None)
      for (_ <- 1 to 10)
        actualUsername = cache.getUsername(consumerKey, accessToken)

      actualUsername must beEqualTo(Some(expectedUsername)).await and {
        there was one(hardPersistence).getUsername(consumerKey, accessToken)
      }
    }
  }
}
