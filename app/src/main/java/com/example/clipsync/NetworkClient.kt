package com.example.clipsync

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object NetworkClient {
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    fun postClip(base: String, text: String, token: String): Boolean {
        val body = """{"text":${text.json()}, "token":${token.json()}}""".toRequestBody(JSON)
        val req = Request.Builder().url("$base/clip").post(body).build()
        client.newCall(req).execute().use { resp -> return resp.isSuccessful }
    }

    fun getClip(base: String): Pair<String, Long>? {
        val req = Request.Builder().url("$base/clip").get().build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return null
            val js = resp.body?.string() ?: return null
            // very small parser to avoid extra deps
            val text = Regex("\"text\"\\s*:\\s*\"(.*)\"").find(js)?.groups?.get(1)?.value?.replace("\\n", "\n")?.replace("\\\"", "\"") ?: ""
            val rev = Regex("\"rev\"\\s*:\\s*(\\d+)").find(js)?.groups?.get(1)?.value?.toLongOrNull() ?: 0L
            return text to rev
        }
    }

    private fun String.json(): String = "\"" + this.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""
}
