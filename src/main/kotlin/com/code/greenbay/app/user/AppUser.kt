package com.code.greenbay.app.user

import io.vertx.core.json.JsonObject

data class AppUser(
    val username: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val idNumber: String,
    val password: String
){
    fun toJson():JsonObject{
        return JsonObject()
            .put("username",username)
            .put("first_name",firstName)
            .put("middle_name",middleName)
            .put("last_name",lastName)
            .put("email",email)
            .put("phone_number",phoneNumber)
            .put("id_number",idNumber)
            .put("password",password)
    }
}
