package com.code.greenbay.app.user

import com.code.greenbay.app.DBService
import com.code.greenbay.app.Utils
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import jdk.jshell.execution.Util

open class AuthService : AbstractVerticle() {
    private lateinit var mongoClient: MongoClient

    init {
        val config = JsonObject().put("db_name", Utils.db_name).put("connection_string", Utils.connection_string)
        mongoClient = MongoClient.create(this.vertx, config)
    }

    fun setUserRoutes(router: Router) {
        router.post("/api/v1/register").handler(this::register)
        router.post("/api/v1/login").handler(this::login)
    }

    private fun register(rc: RoutingContext) {
        rc.request().bodyHandler { buffer ->
            val body = buffer.toJson()
            if (body == null) {
                rc.response().setStatusCode(BAD_REQUEST.code())
                    .end(JsonObject().put("message", "Body is missing").encodePrettily())
                return@bodyHandler
            }
            mongoClient.save(Utils.user_tbl, JsonObject.mapFrom(body)) {
                if (it.succeeded()) {
                    rc.response().setStatusCode(OK.code())
                        .putHeader("Content-Type","application/json")
                        .end(
                            JsonObject().put("message", "User created successfully\nCheck mail to activate")
                                .encodePrettily()
                        )
                }
            }
        }
        rc.response().end("Register route")
    }

    private fun sendMail(mailTemplate:String){

    }

    private fun login(rc: RoutingContext) {
        rc.response().end("Login route")
    }
}