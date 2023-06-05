package com.greenbay.api.utils

import com.greenbay.api.utils.NetworkUtils.Companion.client
import io.netty.handler.codec.http.HttpResponseStatus.*
import io.vertx.core.MultiMap
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import okhttp3.*
import java.io.IOException

const val CONTENT_TYPE = "content-type"
const val APPLICATION_JSON = "application/json"
const val ACCEPT = "accept"
const val TEXT_XML = "text/xml"
const val MULTIPART_FORM = "multipart/form"
const val MAX_BODY_SIZE = 6

class BaseUtils {
    companion object {
        /**
         * Logger for logging purposes
         * @author Jamie Omondi
         * @since 05-06-2023
         */
        private val logger = LoggerFactory.getLogger(BaseUtils::class.java.simpleName)

        /**
         * Returns an encoded string of the response to the client
         * @param code The response code
         * @param message The response message
         * @param payload The data to be sent to the user
         * @author Jamie Omondi
         * @since 05-06-2023
         */
        fun getResponse(code: Int, message: String, payload: JsonObject) =
            JsonObject.of("code", code, "message", message, "payload", payload).encodePrettily()

        /**
         * Returns an encoded string of the response to the client with payload and pagination data
         * @param code The response code
         * @param message The response message
         * @param payload The data to be sent to the user
         * @param pagination The paging data for the client
         * @author Jamie Omondi
         * @since 05-06-2023
         */
        fun getResponse(code: Int, message: String, payload: JsonArray, pagination: JsonObject) =
            JsonObject.of("code", code, "message", message, "payload", payload, "pagination", pagination)
                .encodePrettily()

        /**
         * Returns an encoded string of the response to the client without payload
         * @param code The response code
         * @param message The response message
         * @author Jamie Omondi
         * @since 05-06-2023
         */
        fun getResponse(code: Int, message: String) =
            JsonObject.of("code", code, "message", message).encodePrettily()

        /**
         * Makes a POST API call to an external application and returns the data to the calling function
         * @param url The outside resource to be called
         * @param body The request body
         * @param success The callback function id call succeeds
         * @param fail The callback function when call fails
         * @author Jamie Omondi
         * @since 05-06-2023
         */
        fun makeNetworkCall(
            url: String,
            request: Request,
            body: JsonObject?,
            success: (result: JsonObject) -> Unit,
            fail: (throwable: Throwable) -> Unit
        ) {
            logger.info("makeNetworkCall($url) -->")
            client().newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    success(JsonObject.mapFrom(response.body))
                }

                override fun onFailure(call: Call, e: IOException) {
                    fail(e.cause!!)
                }
            })
            logger.info("makeNetworkCall($url\n${body?.encodePrettily()}) <--")
        }

        /**
         * Does validation of the request-body and other middleware function for un-authenticated routes
         * @param action The method calling this function
         * @param rc The Routing Context from execution block
         * @param task The callback function when all goes well
         * @param values The fields that are mandatory in the request body
         * @author Jamie Omondi
         * @since 05-06-2023
         */
        fun execute(
            action: String,
            rc: RoutingContext,
            task: (body: JsonObject, params: Map<String, String>, response: HttpServerResponse) -> Unit,
            vararg values: String
        ) {
            logger.info("execute(${action}) -->")
            val request: HttpServerRequest = rc.request()
            val requestBody = rc.body().asJsonObject()
            val response = rc.response().apply {
                statusCode = OK.code()
                statusMessage = OK.reasonPhrase()
            }.putHeader(CONTENT_TYPE, APPLICATION_JSON)

            if (requestBody.isEmpty) {
                response.end(getResponse(BAD_REQUEST.code(), "Request body cannot be empty"))
                return
            }
            if (!hasValues(requestBody, *values)) {
                response.end(
                    getResponse(
                        PRECONDITION_FAILED.code(),
                        "Expected fields [${values.contentDeepToString()}], but only got [${requestBody.map.keys}]"
                    )
                )
                return
            }
            val params = rc.request().contextParams()
            bodyHandler(action, requestBody, response) { body, resp ->
                task(body, params, resp)
            }
            logger.info("execute(${action}) <--")
        }

        /**
         * Does body handling of unauthenticated requests
         * @param action The method calling this function
         * @param body The Body from execution block
         * @param task The callback function when all goes well
         * @param values The fields that are mandatory in the request body
         * @author Jamie Omondi
         * @since 05-06-2023
         */
        fun bodyHandler(
            action: String,
            body: JsonObject,
            response: HttpServerResponse,
            task: (body: JsonObject, response: HttpServerResponse) -> Unit
        ) {
            logger.info("bodyHandler($action) -->")
            val bodySize = body.encode().length / 1024
            if (bodySize > MAX_BODY_SIZE) {
                response.end(getResponse(REQUEST_ENTITY_TOO_LARGE.code(), "Request body too large -> [$bodySize MBs]"))
                return
            }
            task(body, response)
            logger.info("bodyHandler($action) <--")
        }

        /**
         * Checks to see if request body has the required fields
         * @param body The request body from client
         * @param values The fields needed in the request body
         * @author Jamie Omondi
         * @since 05-06-2023
         */
        fun hasValues(body: JsonObject, vararg values: String): Boolean {
            if (values.isEmpty())
                return true
            if (body.isEmpty)
                return false
            var isKey = true
            values.forEach {
                isKey = isKey && body.containsKey(it)
            }
            return isKey
        }

    }
}