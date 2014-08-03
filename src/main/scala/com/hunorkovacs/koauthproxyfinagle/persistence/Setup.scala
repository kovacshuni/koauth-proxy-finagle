package com.hunorkovacs.koauthproxyfinagle.persistence

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.model.KeyType._
import com.hunorkovacs.koauthproxyfinagle.persistence.DynamoDBPersistence._
import scala.collection.JavaConverters._

object Setup extends App {

  private val client = createClient

  createTableNonce()

  private def createTableNonce() {
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

  private def createClient = {
    val credentials = new ProfileCredentialsProvider().getCredentials
    val client1 = new AmazonDynamoDBClient(credentials)
    client1.setEndpoint("http://localhost:8000")
    client1
  }
}
