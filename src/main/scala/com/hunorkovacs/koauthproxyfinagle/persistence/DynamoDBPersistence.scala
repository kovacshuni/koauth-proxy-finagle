package com.hunorkovacs.koauthproxyfinagle.persistence

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model._
import com.hunorkovacs.koauth.service.provider.persistence.Persistence
import com.hunorkovacs.koauthproxyfinagle.persistence.DynamoDBPersistence._
import scala.collection.JavaConverters._

import scala.concurrent.{Future, ExecutionContext}

class DynamoDBPersistence(val ec: ExecutionContext) extends Persistence {

  private val client = createClient

  private def createClient = {
    val credentials = new ProfileCredentialsProvider().getCredentials
    val client1 = new AmazonDynamoDBClient(credentials)
    client1.setEndpoint("http://localhost:8000")
    client1
  }

  override def nonceExists(nonce: String, consumerKey: String, token: String)
                          (implicit ec: ExecutionContext): Future[Boolean] = {
    Future {
      val id = new StringBuilder(nonce).append(consumerKey).append(token).mkString
      val key = Map(NoncesAttrId -> new AttributeValue(id)).asJava
      val attributesToGet = List(NoncesAttrId).asJava
      val request = new GetItemRequest(NoncesTable, key, true)
        .withAttributesToGet(attributesToGet)
        .withConsistentRead(true)
      client.getItem(request).getItem != null
    }
  }

  override def authorizeRequestToken(consumerKey: String, requestToken: String, verifierUsername: String, verifier: String)
                                    (implicit ec: ExecutionContext): Future[Unit] = ???

  override def persistNonce(nonce: String, consumerKey: String, token: String)
                           (implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      val id = new StringBuilder(nonce).append(consumerKey).append(token).mkString
      val timestamp = (System.currentTimeMillis / 1000).toString
      val item = Map(NoncesAttrId -> new AttributeValue(id),
        NoncesAttrTimestamp -> new AttributeValue().withN(timestamp)
      ).asJava
      val request = new PutItemRequest(NoncesTable, item)
      client.putItem(request)
      Unit
    }
  }

  override def authenticate(username: String, password: String)
                           (implicit ec: ExecutionContext): Future[Boolean] = ???

  override def getAccessTokenSecret(consumerKey: String, accessToken: String)
                                   (implicit ec: ExecutionContext): Future[Option[String]] = ???

  override def persistAccessToken(consumerKey: String, accessToken: String, accessTokenSecret: String, username: String)
                                 (implicit ec: ExecutionContext): Future[Unit] = ???

  override def whoAuthorizedRequestToken(consumerKey: String, requestToken: String, verifier: String)
                                        (implicit ec: ExecutionContext): Future[Option[String]] = ???

  override def persistRequestToken(consumerKey: String, requestToken: String, requestTokenSecret: String, callback: String)
                                  (implicit ec: ExecutionContext): Future[Unit] = ???

  override def getConsumerSecret(consumerKey: String)
                                (implicit ec: ExecutionContext): Future[Option[String]] = ???

  override def getUsername(consumerKey: String, accessToken: String)
                          (implicit ec: ExecutionContext): Future[Option[String]] = ???

  override def getRequestTokenSecret(consumerKey: String, requestToken: String)
                                    (implicit ec: ExecutionContext): Future[Option[String]] = ???
}

object DynamoDBPersistence {
  val NoncesTable = "nonce"
  val NoncesAttrId = "Id"
  val NoncesAttrTimestamp = "Timestamp"
}
