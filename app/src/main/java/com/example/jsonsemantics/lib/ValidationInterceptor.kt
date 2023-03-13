package com.example.jsonsemantics.lib

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class ValidationInterceptor(private val logger: Logger) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val response = chain.proceed(req)
        val url = req.url().toString()
        if (response.isSuccessful) try {
            startValidation(url, response.peekBody(1000000))
        } catch (_: java.lang.Exception) {
            // log on firebase
        }
        return response
    }

    private fun startValidation(url: String, body: ResponseBody?) {
        val bodyString = body?.string()
        if (bodyString == null) {
            logger.log(url, "$", "empty body")
            return
        } else {
            val target = JsonParser.parseString(bodyString)
            target.validate(url, "$")
        }

    }

    private fun JsonElement.validate(url: String, jsonPath: String) {
        when {
            isJsonPrimitive -> asJsonPrimitive.validatePrim(url, jsonPath)
            isJsonArray -> asJsonArray.validateArr(url, jsonPath)
            isJsonObject -> asJsonObject.validateObj(url, jsonPath)
        }
    }

    private fun JsonObject.validateObj(url: String, jsonPath: String) {
        if (this.isEmpty) logger.log(url, jsonPath, "empty obj")
        else
            this.keySet().forEach { x ->
                this[x].validate(url, "$jsonPath.$x")
            }
    }

    private fun JsonArray.validateArr(url: String, jsonPath: String) {
        if (this.isEmpty)
            logger.log(url, jsonPath, "empty array")
        else
            this.forEachIndexed { index, jsonElement ->
                jsonElement.validate(url, "$jsonPath.[$index]")
            }
    }

    private fun JsonPrimitive.validatePrim(url: String, jsonPath: String) {
        when {
            isString -> asString.validate(url, jsonPath)
        }
    }

    private fun String.validate(url: String, jsonPath: String) {
        if (isBlank()) logger.log(url, jsonPath, "String is blank")
        else if (isEmpty()) logger.log(url, jsonPath, "String is empty")
    }

    interface Logger {
        fun log(url: String, jsonPath: String, message: String)
    }
}