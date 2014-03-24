/**
 * Copyright (C) 2014 Coinport Inc. <http://www.coinport.com>
 *
 */

package com.coinport.coinex.mail

import com.coinport.coinex.data._

import akka.actor._
import akka.event.LoggingReceive

class Mailer(handler: MailHandler) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case request @ SendMailRequest(email, emailType, params) =>
      log.info("{}", request)
      emailType match {
        case EmailType.RegisterVerify =>
          handler.sendRegistrationEmailConfirmation(email, params.toSeq)

        case EmailType.LoginToken =>
          handler.sendLoginToken(email, params.toSeq)

        case EmailType.PasswordResetToken =>
      }
  }
}