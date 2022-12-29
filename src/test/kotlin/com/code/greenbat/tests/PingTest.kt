package com.code.greenbat.tests

import com.code.greenbay.app.MainService
import com.code.greenbay.app.Utils
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod.GET
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class PingTest {
    @BeforeEach
    fun deployService(vertx: Vertx, tc: VertxTestContext) {
        vertx.deployVerticle(MainService())
            .onSuccess { tc.completeNow() }
            .onFailure { failure -> tc.failNow(failure.message) }
    }

    @AfterEach
    fun destroyService(vertx: Vertx, tc: VertxTestContext) {
        vertx.close(tc.succeeding { tc.completeNow() })
    }

//    @Test
//    @DisplayName("Ping test")
//    fun pingTest(vertx: Vertx, tc: VertxTestContext) {
//        WebClient.create(vertx)
//            .get(Utils.app_port,Utils.local_address)
//    }
    @Test
    @DisplayName("Ping test")
    fun pingTest(vertx: Vertx, tc: VertxTestContext) {
        vertx.createHttpClient()
            .request(GET, Utils.app_port, Utils.local_address, "/api/v1/")
            .compose { it.send() }
            .compose { it.body() }
            .onSuccess { resp ->
                tc.verify {
                    assertThat(resp.toJson()).isEqualTo(
                        JsonObject().put(
                            "message",
                            "Server is up and running on port ${Utils.app_port}"
                        )
                    )
                }
                tc.completeNow()
            }
            .onFailure { tc.failNow(it.message) }
    }
}