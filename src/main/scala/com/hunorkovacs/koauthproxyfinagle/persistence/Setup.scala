package com.hunorkovacs.koauthproxyfinagle.persistence

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.model.KeyType._
import com.hunorkovacs.koauthproxyfinagle.persistence.DynamoDBPersistence._
import scala.concurrent.Await.ready
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.collection.JavaConverters._

object Setup extends App {

  private implicit val ec = ExecutionContext.Implicits.global

  private val client = createClient

  createTableNonce()
  createTableConsumer()
  createTableRequestToken()
  createTableAccessToken()
  createTableUser()

  insertDefaultConsumers()
  insertDefaultUsers()
  val pers = new DynamoDBPersistence(ExecutionContext.Implicits.global)
  ready(pers.persistRequestToken("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "nHmH9Qv6vPhZuvLVfofIXoKqpKA6BcSq",
    "S6o9gbm6l6yyR3kcry9kzj40C6mhErmu", "oob"), 1.0 second)
  ready(pers.persistRequestToken("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "DGnMlgdnCxc5ur3ZYX5t1BSjUOJUyqfZ",
    "y6v2ZtztCLH9Yewoeb4NoIXRmWlb74xV", "oob"), 1.0 second)
  ready(pers.authorizeRequestToken("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "DGnMlgdnCxc5ur3ZYX5t1BSjUOJUyqfZ",
    "admin", "W8FMcCtnDZ1Gw1m4"), 1.0 second)
  ready(pers.persistAccessToken("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ", "NDW4H8pFTthDV7kmSkdyYDmiBspabYEW",
    "e3lqNSPq1hU6v7FFnq6p6die6pFIYJU0", "admin"), 1.0 second)

  private def createTableNonce() = {
    val attributeDefinitions= List(new AttributeDefinition(NoncesAttrId, "S")).asJava
    val keySchemaElements = List(new KeySchemaElement(NoncesAttrId, HASH)).asJava
    val provisionedThroughput = new ProvisionedThroughput(10L, 5L)
    val request = new CreateTableRequest()
      .withTableName(NoncesTable)
      .withAttributeDefinitions(attributeDefinitions)
      .withKeySchema(keySchemaElements)
      .withProvisionedThroughput(provisionedThroughput)

    client.createTable(request)
  }

  private def createTableConsumer() = {
    val attributeDefinitions= List(new AttributeDefinition(ConsumerAttrConsumerKey, "S")).asJava
    val keySchemaElements = List(new KeySchemaElement(ConsumerAttrConsumerKey, HASH)).asJava
    val provisionedThroughput = new ProvisionedThroughput(10L, 5L)
    val request = new CreateTableRequest()
      .withTableName(ConsumerTable)
      .withAttributeDefinitions(attributeDefinitions)
      .withKeySchema(keySchemaElements)
      .withProvisionedThroughput(provisionedThroughput)
    client.createTable(request)
  }

  private def createTableRequestToken() = {
    val attributeDefinitions= List(new AttributeDefinition(RequestTokenAttrId, "S")).asJava
    val keySchemaElements = List(new KeySchemaElement(RequestTokenAttrId, HASH)).asJava
    val provisionedThroughput = new ProvisionedThroughput(10L, 5L)
    val request = new CreateTableRequest()
      .withTableName(RequestTokenTable)
      .withAttributeDefinitions(attributeDefinitions)
      .withKeySchema(keySchemaElements)
      .withProvisionedThroughput(provisionedThroughput)
    client.createTable(request)
  }

  private def createTableAccessToken() = {
    val attributeDefinitions= List(new AttributeDefinition(AccessTokenAttrId, "S")).asJava
    val keySchemaElements = List(new KeySchemaElement(AccessTokenAttrId, HASH)).asJava
    val provisionedThroughput = new ProvisionedThroughput(10L, 5L)
    val request = new CreateTableRequest()
      .withTableName(AccessTokenTable)
      .withAttributeDefinitions(attributeDefinitions)
      .withKeySchema(keySchemaElements)
      .withProvisionedThroughput(provisionedThroughput)
    client.createTable(request)
  }

  private def createTableUser() = {
    val attributeDefinitions= List(new AttributeDefinition(UserAttrUsername, "S")).asJava
    val keySchemaElements = List(new KeySchemaElement(UserAttrUsername, HASH)).asJava
    val provisionedThroughput = new ProvisionedThroughput(10L, 5L)
    val request = new CreateTableRequest()
      .withTableName(UserTable)
      .withAttributeDefinitions(attributeDefinitions)
      .withKeySchema(keySchemaElements)
      .withProvisionedThroughput(provisionedThroughput)
    client.createTable(request)
  }

  def insertDefaultConsumers() = {
    val item = Map(ConsumerAttrConsumerKey -> new AttributeValue("OmFjJKNqU4v791CWj6QKaBaiEep0WBxJ"),
      ConsumerAttrConsumerSecret -> new AttributeValue("wr1KLYYH6o5yKFfiyN9ysKkPXcIAim2S")).asJava
    val request = new PutItemRequest(ConsumerTable, item)
    client.putItem(request)
  }

  def insertDefaultUsers() = {
    val item = Map(UserAttrUsername -> new AttributeValue("admin"),
      UserAttrPassword -> new AttributeValue("admin")).asJava
    val request = new PutItemRequest(UserTable, item)
    client.putItem(request)
  }

  private def createClient = {
    val credentials = new ProfileCredentialsProvider().getCredentials
    val client1 = new AmazonDynamoDBClient(credentials)
    client1.setEndpoint("http://localhost:8000")
    client1
  }
}
