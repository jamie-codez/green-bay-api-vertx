package com.greenbay.api.utils

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class NetworkUtils {
    companion object {
        @JvmStatic
        fun client(): OkHttpClient =
            OkHttpClient.Builder()
                .callTimeout(30000, TimeUnit.MILLISECONDS)
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .writeTimeout(15000, TimeUnit.MILLISECONDS)
                .connectTimeout(50000, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor())
                .build()

        @JvmStatic
        fun interceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
                level = HttpLoggingInterceptor.Level.HEADERS
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
                redactHeader("Access-Token")
                redactHeader("Cookie")
            }
    }
}