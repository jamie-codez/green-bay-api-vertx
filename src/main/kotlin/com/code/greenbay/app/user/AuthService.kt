package com.code.greenbay.app.user

import com.code.greenbay.app.DBService
import com.code.greenbay.app.Utils
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.ext.mail.LoginOption
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.MailMessage
import io.vertx.ext.mail.StartTLSOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import jdk.jshell.execution.Util
import java.util.logging.Logger

open class AuthService : AbstractVerticle() {
    private lateinit var mongoClient: MongoClient
    private var logger: Logger = Logger.getLogger(AuthService::class.java.simpleName)

    init {
        val config = JsonObject().put("db_name", Utils.db_name).put("connection_string", Utils.connection_string)
        mongoClient = MongoClient.create(this.vertx, config)
    }

    fun setUserRoutes(router: Router) {
        router.post("/api/v1/register").handler(this::registerClient)
        router.post("/api/v1/login").handler(this::login)
    }

    private fun registerClient(rc: RoutingContext) {
        rc.request().bodyHandler { buffer ->
            val body = buffer.toJsonObject()
            val user =
                JsonObject().put("username", body.getString("username")).put("firstName", body.getString("firstName"))
                    .put("middleName", body.getString("middleName")).put("lastName", body.getString("lastName"))
                    .put("email", body.getString("email")).put("phoneNo", body.getString("phoneNo"))
                    .put("password", body.getString("password"))
            if (body == null) {
                rc.response().setStatusCode(BAD_REQUEST.code())
                    .end(JsonObject().put("message", "Body is missing").encodePrettily())
                return@bodyHandler
            }
            mongoClient.save(Utils.user_tbl, user) {
                if (it.succeeded()) {
                    rc.response().setStatusCode(OK.code()).putHeader("Content-Type", "application/json").end(
                        JsonObject().put("message", "User created successfully\nCheck mail to activate")
                            .encodePrettily()
                    )
                }
            }
        }
        rc.response().end("Register route")
    }

    private fun sendActivationMail(emailAddress: String, name: String, link: String) {
        val config = MailConfig().apply {
            port = 465
            hostname = "smtp.gmail.com"
            isSsl = true
            starttls = StartTLSOptions.OPTIONAL
            username = Utils.username
            password = Utils.password
            login = LoginOption.XOAUTH2
        }
        val htmlText = "<a href=https://$link>Activate Account</a>"
        val mailBody =
            "Hello $name,\nYou have been successfully registered in Green Bay System\nClick the link below to activate " +
                    "your account. ☺\n"
        val mailClient = MailClient.createShared(this.vertx, config, "activation_mail_pool")
        val message = MailMessage().apply {
            from = Utils.from
            to = listOf(emailAddress)
            subject = "Account Activation"
            text = mailBody
            html = "Click link to activate your account $htmlText"
        }
        mailClient.sendMail(message) {
            if (it.succeeded()) {
                logger.info("INFO: Activation mail sent successfully.")
                mongoClient.save(
                    Utils.activation_code_tbl,
                    JsonObject().put("email", emailAddress).put("code", link)
                ) { ar ->
                    if (ar.succeeded()) {
                        logger.info("INFO: Activation link saved successfully")
                    } else {
                        logger.info("INFO: Failed to save activation link to database")
                    }
                }
            } else {
                logger.info("INFO: Error sending activation mail")
            }
        }

    }

    private fun login(rc: RoutingContext) {
        rc.response().end("Login route")
    }
}