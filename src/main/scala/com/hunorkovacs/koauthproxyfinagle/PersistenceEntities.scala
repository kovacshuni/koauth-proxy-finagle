package com.hunorkovacs.koauthproxyfinagle

import java.util.Date

case class Consumer(consumerKey: String,
                    consumerSecret: String,
                    appId: Int,
                    ownerUsername: String)

case class RequestToken(consumerKey: String,
                       requestToken: String,
                       requestTokenSecret: String,
                       callback: String,
                       verifierUsername: Option[String],
                       verifier: Option[String])

case class AccessToken(consumerKey: String,
                       accessToken: String,
                       accessTokenSecret: String,
                       username: String)

case class Nonce(nonce: String,
                 time: Date,
                 consumerKey: String,
                 token: String)
