package com.greenbay.api.services.user

data class AppUser(
    val uid:String,
    val username:String,
    val firstName:String,
    val lastName:String,
    val email:String,
    val phoneNumber:String,
    val verified:Boolean,
    val roles:List<Role>
)

data class Role(
    val roleName:String,
    val roleDescription:String
)
