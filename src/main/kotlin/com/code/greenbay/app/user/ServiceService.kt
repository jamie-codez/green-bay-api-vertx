package com.code.greenbay.app.user

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod.*
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler

class ServiceService : AuthService() {
    override fun start(startPromise: Promise<Void>?) {
        super.start(startPromise)
        val router = Router.router(this.vertx)
        router.route().handler(CorsHandler.create("*").apply {
            allowedHeader("Access-Control-Allow-Origin")
            allowedHeader("Content-Type")
            allowedMethods(setOf(POST, GET, PUT, DELETE, PATCH))
        })
        router.route().handler(BodyHandler.create())
        router.get("/").handler(this::ping)
        setUserRoutes(router)
        vertx.createHttpServer().requestHandler(router)
            .listen(9000) { http ->
                if (http.succeeded()) {
                    println("Server started on port 9000")
                } else {
                    println("Server failed to start")
                }
            }
    }

    private fun ping(rc: RoutingContext) {
        rc.response().putHeader("Content-Type", "text/plain").setStatusCode(HttpResponseStatus.OK.code())
            .end("Server is up and running on port ${rc.request().connection().localAddress().port()}")
    }

    override fun stop() {
        super.stop()
    }
}

fun main(arg: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(ServiceService())
}