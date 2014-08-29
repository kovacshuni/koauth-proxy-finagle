package com.hunorkovacs.koauthproxyfinagle.persistence

import scala.concurrent.{Future, ExecutionContext}

trait HardPersistence {

  def getConsumerSecret(consumerKey: String)
                       (implicit ec: ExecutionContext): Future[Option[String]]

  def getAccessTokenSecret(consumerKey: String, accessToken: String)
                          (implicit ec: ExecutionContext): Future[Option[String]]

  def getUsername(consumerKey: String, accessToken: String)
                 (implicit ec: ExecutionContext): Future[Option[String]]
}
