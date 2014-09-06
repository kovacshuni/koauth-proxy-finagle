package com.hunorkovacs.koauthproxyfinagle.persistence

import scala.concurrent.Future

trait HardPersistence {

  def getConsumerSecret(consumerKey: String): Future[Option[String]]

  def getAccessTokenSecret(consumerKey: String, accessToken: String): Future[Option[String]]

  def getUsername(consumerKey: String, accessToken: String): Future[Option[String]]
}
