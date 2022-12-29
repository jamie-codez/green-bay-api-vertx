package com.code.greenbay.app

import io.netty.handler.codec.http.HttpResponseStatus.OK
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext

class DBService : AbstractVerticle() {
    private lateinit var mongoClient: MongoClient
    private fun initDB() {
        val config = JsonObject().put("db_name", "green_bay").put("connection_string", "mongo://localhost:27017/")
        mongoClient = MongoClient.create(this.vertx, config)
    }

    init {
        initDB()
    }

    fun saveUser(rc: RoutingContext, body: JsonObject) {
        mongoClient.save(Utils.user_tbl, body) {
            if (it.succeeded()) {
                rc.response().setStatusCode(OK.code()).end(
                    JsonObject().put("message", "User created successfully\nCheck mail to activate").encodePrettily()
                )
            }
        }
    }
}