package com.greenbay.api.services.user

import com.greenbay.api.utils.DatabaseUtils
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

class ServiceEngine(vertx:Vertx) :Service {
    private val dbUtil = DatabaseUtils(vertx)
    override fun createUser(appUser: AppUser,success:(result:String)->Unit,fail:(throwable:Throwable)->Unit) {
        val user = JsonObject.mapFrom(appUser)
        dbUtil.save("app_user",user,{
            success(it)
        },{
            fail(it)
        })
    }

    override fun getUsers() {
        TODO("Not yet implemented")
    }
}