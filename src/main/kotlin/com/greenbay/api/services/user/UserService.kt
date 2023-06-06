package com.greenbay.api.services.user

import com.greenbay.api.utils.BaseUtils.Companion.execute
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class UserService : AbstractVerticle() {
    val service = ServiceEngine(this.getVertx())

    fun setUserRoutes(router: Router) {
        router.post("/user").handler(::createUser)
    }

    private fun createUser(rc: RoutingContext) {
        execute("createUser", rc, { body, params, response ->
            try {
                val user = body.mapTo(AppUser::class.java)
            }catch (ex:Exception){
                println(ex.message)
            }
        }, "username", "firstName", "lastName", "email", "phone", "")
    }
}