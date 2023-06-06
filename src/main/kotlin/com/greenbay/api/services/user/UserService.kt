package com.greenbay.api.services.user

import io.vertx.core.AbstractVerticle

class UserService:AbstractVerticle() {
    val service = ServiceEngine(this.getVertx())

}