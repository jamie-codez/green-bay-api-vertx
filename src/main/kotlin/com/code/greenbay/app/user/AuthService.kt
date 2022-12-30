package com.code.greenbay.app.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.code.greenbay.app.DBService
import com.code.greenbay.app.Utils
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpMethod.*
import io.vertx.core.json.JsonObject
import io.vertx.ext.mail.LoginOption
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.MailMessage
import io.vertx.ext.mail.StartTLSOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.CorsHandler
import jdk.jshell.execution.Util
import java.time.Instant
import java.util.*
import java.util.logging.Logger
import kotlin.collections.HashMap

class AuthService : AbstractVerticle() {
    private var mongoClient: MongoClient
    private var logger: Logger = Logger.getLogger(AuthService::class.java.simpleName)

    init {
        val config = JsonObject().put("db_name", Utils.db_name).put("connection_string", Utils.connection_string)
        mongoClient = MongoClient.create(Vertx.vertx(), config)
    }

    override fun start() {
        super.start()
        val router = Router.router(this.vertx)
        router.route().handler(CorsHandler.create("*").apply {
            allowedHeader("Access-Control-Allow-Origin")
            allowedHeader("Content-Type")
            allowedHeader("access-token")
            allowedMethods(setOf(POST, GET, PUT, PATCH, DELETE))
        })
        router.post("/api/v1/register").handler(this::registerTenant)
        router.post("/api/v1/login").handler(this::loginTenant)
        router.get("/api/v1/account/activate/:code").handler(this::activateAccount)
        this.vertx.createHttpServer().requestHandler(router).listen(8000) {
            if (it.succeeded()) {
                logger.info("INFO: User service up and running")
            } else {
                logger.info("INFO: User service failed to start")
            }
        }

    }


    private fun registerTenant(rc: RoutingContext) {
        rc.request().bodyHandler { buffer ->
            val body = buffer.toJsonObject()
            mongoClient.findOne(Utils.user_tbl, JsonObject().put("email", body.getString("email")), null) { result ->
                if (result.succeeded() && result.result() != null) {
                    rc.response().setStatusCode(BAD_REQUEST.code()).putHeader("Content-Type", "application/json")
                        .end(JsonObject().put("message", "user already exist").encodePrettily())
                } else {
                    val user = JsonObject().put("username", body.getString("username"))
                        .put("firstName", body.getString("firstName")).put("middleName", body.getString("middleName"))
                        .put("lastName", body.getString("lastName")).put("email", body.getString("email"))
                        .put("phoneNo", body.getString("phoneNo")).put("password", body.getString("password"))
                        .put("activated", false)
                    if (body == null) {
                        rc.response().setStatusCode(BAD_REQUEST.code())
                            .end(JsonObject().put("message", "Body is missing").encodePrettily())
                        return@findOne
                    }
                    mongoClient.save(Utils.user_tbl, user) {
                        if (it.succeeded()) {
                            rc.response().setStatusCode(OK.code()).putHeader("Content-Type", "application/json").end(
                                JsonObject().put("message", "User created successfully\nCheck mail to activate")
                                    .encodePrettily()
                            )
                            val code = UUID.randomUUID().toString()
                            val link = "${rc.request().localAddress().hostAddress()}:${
                                rc.request().localAddress().port()
                            }/api/v1/account/activate/$code"
                            val htmlText = "<a href=https://$link>Activate Account</a>"
                            val mailBody =
                                "Hello ${user.getString("username")},\nYou have been successfully registered in Green Bay System\nClick the link below to activate " + "your account. ☺\n"
                            val html = "Click link to activate your account $htmlText"
                            Utils.sendMail(
                                command = "activation",
                                emailAddress = user.getString("email"),
                                link = link,
                                code = code,
                                mailSubject = "Account Activation",
                                mailBody = mailBody,
                                htmlString = html,
                                logger = logger,
                                mongoClient = mongoClient,
                                vertx = this.vertx
                            )
                        } else {
                            logger.info("ERROR: Error adding user to database")
                        }
                    }
                }
            }
        }
    }


    private fun loginTenant(rc: RoutingContext) {
        rc.request().bodyHandler {
            val body = it.toJsonObject()
            val loginForm =
                JsonObject().put("email", body.getString("email")).put("password", body.getString("password"))
            mongoClient.findOne(
                Utils.user_tbl, loginForm, JsonObject().put("password", loginForm.getString("password"))
            ) { result ->
                if (result.succeeded()) {
                    val payload = HashMap<String, String>()
                    payload["email"] = loginForm.getString("email")
                    payload["username"] = result.result().getString("username")
                    val refreshToken = JWT.create().withPayload(payload).withSubject(result.result().getString("email"))
                        .withIssuedAt(Date(System.currentTimeMillis()))
                        .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 24 * 7 * 4 * 1000L))
                        .withIssuer(Utils.jwt_issuer).sign(Algorithm.HMAC512(Utils.jwt_secret))

                    val accessToken = JWT.create().withPayload(payload).withSubject(result.result().getString("email"))
                        .withIssuedAt(Date(System.currentTimeMillis()))
                        .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 24 * 7 * 1000))
                        .withIssuer(Utils.jwt_issuer).sign(Algorithm.HMAC512(Utils.jwt_secret))

                    rc.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase())
                        .putHeader("Content-Type", "application/json").putHeader("access-token", accessToken)
                        .putHeader("refresh-token", refreshToken).end(
                            JsonObject().put("message", "Login successful").encodePrettily()
                        )
                } else {
                    rc.response().setStatusCode(BAD_REQUEST.code()).setStatusMessage(BAD_REQUEST.reasonPhrase())
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject().put("message", "Invalid credentials").encodePrettily())
                }
            }

        }
    }

    private fun activateAccount(rc: RoutingContext) {
        val code = rc.request().getParam("code")
        mongoClient.findOne(Utils.activation_code_tbl, JsonObject().put("code", code), null) { ar ->
            if (ar.succeeded() && ar.result() != null) {
                mongoClient.findOneAndUpdate(
                    Utils.user_tbl,
                    JsonObject().put("email", ar.result().getString("email")),
                    JsonObject().put("\$set", JsonObject().put("activated", true))
                ) { res ->
                    if (res.succeeded()) {
                        mongoClient.findOneAndDelete(Utils.activation_code_tbl, JsonObject().put("code", code)) {
                            if (it.succeeded()) {
                                rc.response()
                                    .putHeader("Content-Type", "application/json").setStatusCode(OK.code())
                                    .setStatusMessage(OK.reasonPhrase()).end(
                                        JsonObject().put("message", "Account activated successfully 😊")
                                            .encodePrettily()
                                    )
                            }
                        }
                    }
                }
            } else {
                rc.response().putHeader("Content-Type", "application/json")
                    .setStatusCode(OK.code())
                    .setStatusMessage(OK.reasonPhrase())
                    .end(JsonObject().put("message", "Activation code already used").encodePrettily())
            }
        }
    }
}