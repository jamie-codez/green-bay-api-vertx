package com.greenbay.api.services.user

interface Service {
    fun createUser(appUser: AppUser,success:(result:String)->Unit,fail:(throwable:Throwable)->Unit)
    fun getUsers()
}