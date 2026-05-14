package com.matrusneh.app.util

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiApiHelper {
    private const val TAG = "GeminiApiHelper"
    // Note: It's better to secure this key, but keeping it here as per original code for now.
    private const val GEMINI_API_KEY = "AIzaSyC-Ig3RBiw5KtpO4hhc0rJ2NbQkbn75zEc"
    // Using v1 and gemini-1.5-flash for better stability and free tier support
    private const val API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$GEMINI_API_KEY"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
        
    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun fetchHealthTip(week: Int, language: String, topic: String): String? {
        val prompt = "You are a maternal health expert. Give ONE specific, practical health tip for a pregnant woman in week $week of pregnancy. If language is $language, respond only in $language script. Tip should be about: $topic. Keep it under 80 words. Be warm and encouraging. Do not use markdown formatting like asterisks."
        
        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(API_URL)
            .post(jsonBody.toString().toRequestBody(JSON))
            .header("Content-Type", "application/json")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string()
                Log.d(TAG, "Response Code: ${response.code}")
                
                if (!response.isSuccessful) {
                    Log.e(TAG, "API Error: $bodyString")
                    return null
                }
                
                if (bodyString == null) return null
                
                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    Log.w(TAG, "No candidates found in response")
                    return null
                }
                
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                if (content == null) {
                    Log.w(TAG, "Content missing. Finish reason: ${candidate.optString("finishReason")}")
                    return "Tip could not be generated due to safety filters. Please try another topic."
                }
                
                content.getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                    .trim()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching tip", e)
            null
        }
    }
}
