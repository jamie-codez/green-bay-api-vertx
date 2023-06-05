package com.greenbay.api.utils

import io.vertx.core.http.HttpServerRequest

fun HttpServerRequest.contextParams(): Map<String, String> {
    val map = HashMap<String, String>()
    this.query().split("&").forEach {
        it.split("=").forEach { q ->
            map[q[0].toString()] = q[1].toString()
        }
    }
    return map
}