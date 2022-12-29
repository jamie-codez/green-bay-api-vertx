package com.code.greenbay.app

import com.code.greenbay.app.user.AuthService
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.*
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod.*
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler

class MainService : AuthService() {
    override fun start(startPromise: Promise<Void>?) {
        super.start(startPromise)
        val router = Router.router(this.vertx)
        router.route().handler(CorsHandler.create("*").apply {
            allowedHeader("Access-Control-Allow-Origin")
            allowedHeader("Content-Type")
            allowedMethods(setOf(POST, GET, PUT, DELETE, PATCH))
        })
        router.route().handler(BodyHandler.create())
        router.get("/api/v1/").handler(this::ping)
        setUserRoutes(router)
        vertx.createHttpServer().requestHandler(router)
            .listen(Utils.app_port) { http ->
                if (http.succeeded()) {
                    println("Server started on port ${Utils.app_port}")
                } else {
                    println("Server failed to start")
                }
            }
    }

    private fun ping(rc: RoutingContext) {
        rc.response().putHeader("Content-Type", "application/json")
            .setStatusCode(OK.code())
            .end(
                JsonObject().put(
                    "message",
                    "Server is up and running on port ${rc.request().connection().localAddress().port()}"
                ).encodePrettily()
            )
    }

    override fun stop() {
        super.stop()
    }
}

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainService())
}