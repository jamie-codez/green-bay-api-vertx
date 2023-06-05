package com.code.greenbay.app.user

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

open class AuthService : AbstractVerticle() {
    fun setUserRoutes(router: Router) {
        router.post("/api/v1/register").handler(this::register)
        router.post("/api/v1/login").handler(this::login)
    }

    private fun register(routingContext: RoutingContext) {
        routingContext.response().end("Register route")
    }

    private fun login(routingContext: RoutingContext) {
        routingContext.response().end("Login route")
    }
}