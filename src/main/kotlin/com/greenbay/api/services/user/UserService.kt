package com.greenbay.api.services.user

import com.greenbay.api.utils.BaseUtils.Companion.execute
import com.greenbay.api.utils.BaseUtils.Companion.getResponse
import io.netty.handler.codec.http.HttpResponseStatus.CREATED
import io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR
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
                service.createUser(user, {
                    response.end(getResponse(CREATED.code(),"User created successfully"))
                }, {
                    response.end(getResponse(INTERNAL_SERVER_ERROR.code(), "Error occurred try again"))
                })
            } catch (ex: Exception) {
                println(ex.message)
                response.end(getResponse(INTERNAL_SERVER_ERROR.code(), "${ex.message}"))
            }
        }, "username", "firstName", "lastName", "email", "phone", "")
    }
}