package com.code.greenbat.tests

import com.code.greenbay.app.Utils
import com.code.greenbay.app.user.AuthService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.junit5.web.TestRequest.testRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class AuthTests {
    @BeforeEach
    fun deployService(vertx: Vertx, tc: VertxTestContext) {
        vertx.deployVerticle(AuthService())
            .onSuccess { tc.completeNow() }
            .onFailure { failure -> tc.failNow(failure.message) }
    }

    @AfterEach
    fun destroyService(vertx: Vertx, tc: VertxTestContext) {
        vertx.close(tc.succeeding { tc.completeNow() })
    }

    @Test @DisplayName("Sign up test")
    fun signUp(vertx: Vertx, tc: VertxTestContext) {
        val body = JsonObject()
            .put("username", "Test User")
            .put("firstName", "Test")
            .put("middleName", " ")
            .put("lastName", "User")
            .put("email", "testemail@mail.com")
            .put("phoneNumber", "0712345678")
            .put("natIdNumber", "1234567890")
            .put("password", "Password-1234")
        WebClient.create(vertx)
            .post(Utils.app_port, Utils.local_address, "/api/v1/register")
            .`as`(BodyCodec.jsonObject())
            .putHeader("Content-Type", "application/json")
            .sendJsonObject(body)
            .onSuccess {
                tc.verify {
                   assertThat(it.statusCode()).isEqualTo(200)
                   assertThat(it.body().getString("message")).isEqualTo("User created successfully")
                    tc.completeNow()
                }
            }
            .onFailure { tc.failNow(it.message) }
        tc.completeNow()
    }
    @Test @DisplayName("Login test") fun loginTest(vertx: Vertx,testContext: VertxTestContext){
        val client = WebClient.create(vertx)
        val body = JsonObject()
            .put("username", "Test User")
            .put("firstName", "Test")
            .put("middleName", " ")
            .put("lastName", "User")
            .put("email", "testemail@mail.com")
            .put("phoneNumber", "0712345678")
            .put("natIdNumber", "1234567890")
            .put("password", "Password-1234")
        testRequest(client
            .post("/api/v1/login")
            .putHeader("Content-Type","application/json")
            ).sendJson(body,testContext)
            .onSuccess {
                assertThat(it.statusCode()).isEqualTo(200)
                testContext.completeNow()
            }
            .onFailure { testContext.failNow(it.message) }

    }
}