/**
 * Copyright (C) 2014 Coinport Inc. <http://www.coinport.com>
 *
 */

package com.coinport.coinex.apiauth

import akka.event.LoggingReceive
import com.coinport.coinex.common._
import com.coinport.coinex.data._
import com.coinport.coinex.util.MHash
import com.google.common.io.BaseEncoding
import akka.persistence.Persistent

class ApiAuthView(seed: String) extends ExtendedView {
  override val processorId = "coinex_aap"
  override val viewId = "api_auth_view"

  val manager = new ApiAuthManager(seed)

  def receive = LoggingReceive {
    case QueryApiSecrets(userId, identifier) =>
      identifier match {
        case Some(identifier) => sender ! QueryApiSecretsResult(userId, manager.getSecret(identifier))
        case None => sender ! QueryApiSecretsResult(userId, manager.getUserSecrets(userId))
      }

    case p @ Persistent(DoAddNewApiSecret(userId), _) =>
      manager.addNewSecret(userId) match {
        case Left(code) => sender ! ApiSecretOperationResult(code, manager.getUserSecrets(userId))
        case Right(_) => sender ! ApiSecretOperationResult(ErrorCode.Ok, manager.getUserSecrets(userId))
      }

    case p @ Persistent(DoDeleteApiSecret(secret), _) =>
      manager.deleteSecret(secret) match {
        case Left(code) => sender ! ApiSecretOperationResult(code, Nil)
        case Right(_) => sender ! ApiSecretOperationResult(ErrorCode.Ok, Nil)
      }
  }
}

class ApiAuthManager(initialSeed: String) extends Manager[ApiSecretState](ApiSecretState(seed = initialSeed)) {
  val MAX_SECRETS_PER_USER = 50

  def getUserSecrets(userId: Long) = {
    val secrets = state.userSecretMap.getOrElse(userId, Nil)
    secrets.map { secret => secret.copy(userId = Some(userId)) }
  }

  def getSecret(identifier: String) = {
    state.identifierLookupMap.get(identifier) match {
      case Some(secret) => Seq(secret)
      case None => Nil
    }
  }

  def addNewSecret(userId: Long): Either[ErrorCode, ApiSecret] = {
    val existing = state.userSecretMap.getOrElse(userId, Nil)
    if (existing.size >= MAX_SECRETS_PER_USER) {
      Left(ErrorCode.TooManySecrets)
    } else {
      val (identifier, secret) = generateNewSecret()
      val identifierLookupMap = state.identifierLookupMap + (identifier -> ApiSecret(secret, None, Some(userId)))

      val secrets = existing :+ ApiSecret(secret, Some(identifier), None)
      val userSecretMap = state.userSecretMap + (userId -> secrets)

      state = state.copy(identifierLookupMap = identifierLookupMap, userSecretMap = userSecretMap)
      Right(ApiSecret(secret, Some(identifier), Some(userId)))
    }
  }

  def deleteSecret(secret: ApiSecret): Either[ErrorCode, ApiSecret] = {
    if (secret.userId.isEmpty || secret.identifier.isEmpty) {
      Left(ErrorCode.InvalidSecret)
    } else {
      val userId = secret.userId.get
      val identifier = secret.identifier.get
      val existing = state.userSecretMap.getOrElse(userId, Nil)
      val secrets = existing.filterNot(_.identifier == identifier)
      val userSecretMap = state.userSecretMap + (userId -> secrets)
      val identifierLookupMap = state.identifierLookupMap - identifier

      state = state.copy(identifierLookupMap = identifierLookupMap, userSecretMap = userSecretMap)
      Right(secret)
    }
  }

  private def generateNewSecret(): (String, String) = {
    val x1 = MHash.sha256Base64(state.seed)
    val x2 = MHash.sha256Base64(x1)
    val x3 = MHash.sha256Base64(x2)

    val identifier = Math.abs(MHash.murmur3(x1)).toString
    val secret = "%x%x".format(MHash.murmur3(x2), MHash.murmur3(x3))
    state = state.copy(seed = MHash.sha256Base64(x3))
    (identifier, secret)
  }
}