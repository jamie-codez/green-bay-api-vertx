package com.code.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

class MainVerticle : AbstractVerticle() {
    override fun start(startPromise: Promise<Void>?) {
        super.start(startPromise)
        val router = Router.router(this.vertx)
        router.route().handler { context ->
            val address = context.request().connection().remoteAddress().toString()
            val queryParams = context.queryParams()
            val name = queryParams["name"] ?: "unknown"
            context.json(
                JsonObject().put("name", name)
                    .put("address", address)
                    .put("message", "Hello $name connected from $address")
            )
        }

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(9000)
            .onSuccess { server ->
                println("HTTP server started on port ${server.actualPort()}")
            }
    }

    override fun stop(stopPromise: Promise<Void>?) {
        super.stop(stopPromise)
    }

}
