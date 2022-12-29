package com.code.greenbat.tests

import com.code.greenbay.app.Utils
import com.code.greenbay.app.user.AuthService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class AuthTests {
    @BeforeEach
    fun deployService(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(AuthService())
            .onSuccess { testContext.completeNow() }
            .onFailure { failure -> testContext.failNow(failure.message) }
    }

    @AfterEach
    fun destroyService(vertx: Vertx, testContext: VertxTestContext) {
        vertx.close(testContext.succeeding { testContext.completeNow() })
    }

    @Test
    fun signUp(vertx: Vertx, testContext: VertxTestContext) {
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
            .post(Utils.app_port,"http://127.0.0.1","/api/v1/register")
            .`as`(BodyCodec.jsonObject())
            .putHeader("Content-Type","application/json")
            .sendJsonObject(body,testContext.succeeding { res ->
                testContext.verify {
                    val response = res.body()
                    assertThat(res.statusCode()).isEqualTo(200)
                    assertThat(response.getString("message")).isEqualTo("User created successfully")
                }
            })
    }
}