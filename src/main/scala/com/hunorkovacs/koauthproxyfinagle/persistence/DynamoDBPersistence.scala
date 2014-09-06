package com.hunorkovacs.koauthproxyfinagle.persistence

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeAction.PUT
import com.amazonaws.services.dynamodbv2.model._
import com.google.inject.Inject
import com.hunorkovacs.koauthproxyfinagle.persistence.DynamoDBPersistence._
import scala.collection.JavaConverters._

import scala.concurrent.{Future, ExecutionContext}

class DynamoDBPersistence @Inject() (private val client: AmazonDynamoDBClient,
                          private val ec: ExecutionContext) extends HardPersistence {

  implicit private val implicitEc = ec

  def authorizeRequestToken(consumerKey: String, requestToken: String, verifierUsername: String,
                            verifier: String): Future[Unit] = {
    Future {
      val id = new StringBuilder(consumerKey).append(requestToken).mkString
      val key = Map(RequestTokenAttrId -> new AttributeValue(id)).asJava
      val attributeUpdates = Map(RequestTokenAttrVerifier -> new AttributeValueUpdate(new AttributeValue(verifier), PUT),
        RequestTokenAttrVerifierUsername -> new AttributeValueUpdate(new AttributeValue(verifierUsername), PUT)).asJava
      val request = new UpdateItemRequest(RequestTokenTable, key, attributeUpdates)
      client.updateItem(request)
      Unit
    }
  }

  def getPassword(username: String): Future[Option[String]] = {
    Future {
      val key = Map(UserAttrUsername -> new AttributeValue(username)).asJava
      val attributesToGet = List(UserAttrPassword).asJava
      val request = new GetItemRequest(UserTable, key)
        .withAttributesToGet(attributesToGet)
      val item = client.getItem(request).getItem
      if (item != null) Some(item.asScala(UserAttrPassword).getS)
      else None
    }
  }

  def whoAuthorizedRequestToken(consumerKey: String, requestToken: String, verifier: String): Future[Option[String]] = {
    Future {
      val id = new StringBuilder(consumerKey).append(requestToken).mkString
      val key = Map(RequestTokenAttrId -> new AttributeValue(id)).asJava
      val attributesToGet = List(RequestTokenAttrVerifierUsername, RequestTokenAttrVerifier).asJava
      val request = new GetItemRequest(RequestTokenTable, key)
        .withAttributesToGet(attributesToGet)
        .withConsistentRead(true)
      val item = client.getItem(request).getItem
      if (item != null) {
        val sItem = item.asScala
        if (sItem.isDefinedAt(RequestTokenAttrVerifier)
          && verifier.equals(sItem(RequestTokenAttrVerifier).getS)) {
          Some(sItem(RequestTokenAttrVerifierUsername).getS)
        }
        else None
      }
      else None
    }
  }

  def persistRequestToken(consumerKey: String, requestToken: String, requestTokenSecret: String,
                          callback: String): Future[Unit] = {
    Future {
      val id = new StringBuilder(consumerKey).append(requestToken).mkString
      val item = Map(RequestTokenAttrId -> new AttributeValue(id),
        RequestTokenAttrRequestTokenSecret -> new AttributeValue(requestTokenSecret),
        RequestTokenAttrCallback -> new AttributeValue(callback)
      ).asJava
      val request = new PutItemRequest(RequestTokenTable, item)
      client.putItem(request)
      Unit
    }
  }

  def persistAccessToken(consumerKey: String, accessToken: String, accessTokenSecret: String,
                         username: String): Future[Unit] = {
    Future {
      val id = new StringBuilder(consumerKey).append(accessToken).mkString
      val item = Map(AccessTokenAttrId -> new AttributeValue(id),
        AccessTokenAttrAccessTokenSecret -> new AttributeValue(accessTokenSecret),
        AccessTokenAttrUsername -> new AttributeValue(username)
      ).asJava
      val request = new PutItemRequest(AccessTokenTable, item)
      client.putItem(request)
      Unit
    }
  }

  def getRequestTokenSecret(consumerKey: String, requestToken: String): Future[Option[String]] = {
    Future {
      val id = new StringBuilder(consumerKey).append(requestToken).mkString
      val key = Map(RequestTokenAttrId -> new AttributeValue(id)).asJava
      val attributesToGet = List(RequestTokenAttrRequestTokenSecret).asJava
      val request = new GetItemRequest(RequestTokenTable, key)
        .withAttributesToGet(attributesToGet)
        .withConsistentRead(true)
      val item = client.getItem(request).getItem
      if (item != null) Some(item.asScala(RequestTokenAttrRequestTokenSecret).getS)
      else None
    }
  }

  override def getConsumerSecret(consumerKey: String): Future[Option[String]] = {
    Future {
      val key = Map(ConsumerAttrConsumerKey -> new AttributeValue(consumerKey)).asJava
      val attributesToGet = List(ConsumerAttrConsumerSecret).asJava
      val request = new GetItemRequest(ConsumerTable, key)
        .withAttributesToGet(attributesToGet)
        .withConsistentRead(true)
      val item = client.getItem(request).getItem
      if (item != null) Some(item.asScala(ConsumerAttrConsumerSecret).getS)
      else None
    }
  }

  override def getAccessTokenSecret(consumerKey: String, accessToken: String): Future[Option[String]] = {
    Future {
      val id = new StringBuilder(consumerKey).append(accessToken).mkString
      val key = Map(AccessTokenAttrId -> new AttributeValue(id)).asJava
      val attributesToGet = List(AccessTokenAttrAccessTokenSecret).asJava
      val request = new GetItemRequest(AccessTokenTable, key)
        .withAttributesToGet(attributesToGet)
        .withConsistentRead(true)
      val item = client.getItem(request).getItem
      if (item != null) Some(item.asScala(AccessTokenAttrAccessTokenSecret).getS)
      else None
    }
  }

  override def getUsername(consumerKey: String, accessToken: String): Future[Option[String]] = {
    Future {
      val id = new StringBuilder(consumerKey).append(accessToken).mkString
      val key = Map(AccessTokenAttrId -> new AttributeValue(id)).asJava
      val attributesToGet = List(AccessTokenAttrUsername).asJava
      val request = new GetItemRequest(AccessTokenTable, key)
        .withAttributesToGet(attributesToGet)
        .withConsistentRead(true)
      val item = client.getItem(request).getItem
      if (item != null) Some(item.asScala(AccessTokenAttrUsername).getS)
      else None
    }
  }
}

object DynamoDBPersistence {
  val ConsumerTable = "Oauth1Consumer"
  val ConsumerAttrConsumerKey = "ConsumerKey"
  val ConsumerAttrConsumerSecret = "ConsumerSecret"
  val RequestTokenTable = "Oauth1RequestToken"
  val RequestTokenAttrId = "Id"
  val RequestTokenAttrRequestTokenSecret = "Secret"
  val RequestTokenAttrVerifierUsername = "VerifierUsername"
  val RequestTokenAttrVerifier = "Verifier"
  val RequestTokenAttrCallback = "Callback"
  val AccessTokenTable = "Oauth1AccessToken"
  val AccessTokenAttrId = "Id"
  val AccessTokenAttrAccessTokenSecret = "Secret"
  val AccessTokenAttrUsername = "Username"
  val UserTable = "Oauth1User"
  val UserAttrUsername = "Username"
  val UserAttrPassword = "Password"
}
