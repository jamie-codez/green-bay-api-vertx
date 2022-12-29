package com.code.greenbay.app

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mail.*
import io.vertx.ext.mongo.MongoClient
import java.util.logging.Logger

class Utils {
    companion object {
        const val jwt_issuer = "greenbay.com"
        const val jwt_secret = "jwt-secret"
        const val app_port = 8001
        const val local_address = "127.0.0.1"
        const val db_name = "green-bay"
        const val connection_string = "mongodb://localhost:27017"
        const val username = ""
        const val password = ""
        const val from = "noreply@greenbay.com"
        const val user_tbl = "users"
        const val activation_code_tbl = "activation_code"
        const val house_tbl = "houses"
        const val tenant_tbl = "tenants"
        const val payment_tbl = "payments"
        const val admin_tbl = "admin"
        const val messages_tbl = "messages"
        const val repairs_tbl = "repairs"
        fun sendMail(
            command: String,
            emailAddress: String,
            mailSubject: String,
            link: String? = "",
            mailBody: String,
            htmlString: String,
            logger: Logger,
            mongoClient: MongoClient? = null,
            vertx: Vertx
        ) {
            val config = MailConfig().apply {
                port = 465
                hostname = "smtp.gmail.com"
                isSsl = true
                starttls = StartTLSOptions.OPTIONAL
                username = Utils.username
                password = Utils.password
                login = LoginOption.XOAUTH2
            }

            val mailClient = MailClient.createShared(vertx, config, "activation_mail_pool")
            val message = MailMessage().apply {
                from = Utils.from
                to = listOf(emailAddress)
                subject = mailSubject
                text = mailBody
                html = htmlString
            }
            mailClient.sendMail(message) {
                if (it.succeeded()) {
                    logger.info("INFO: Mail sent successfully.")
                    if (command == "activation") {
                        mongoClient!!.save(
                            Utils.activation_code_tbl, JsonObject().put("email", emailAddress).put("code", link)
                        ) { ar ->
                            if (ar.succeeded()) {
                                logger.info("INFO: Activation link saved successfully")
                            } else {
                                logger.info("INFO: Failed to save activation link to database")
                            }
                        }
                    }

                } else {
                    logger.info("INFO: Error sending mail")
                }
            }
        }
    }


}