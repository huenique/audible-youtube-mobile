package com.huenique.audibleyoutube

import okhttp3.*
import java.io.IOException


class AudibleYoutubeApi {
    private var httpClient = OkHttpClient()

    fun downloadVideo(query: String) {
        val request = Request.Builder()
            .url(download.format(query))
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println(response)
        }
    }

    fun searchVideo(query: String) {
        val request = Request.Builder()
            .url(search.format(query, 5))
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    println(response.body!!.string())
                }
            }
        })
    }

    companion object Url {
        private const val baseUrl = "https://audible-youtube.herokuapp.com"
        const val search = "$baseUrl/search?query=%s&size=%d"
        const val download = "$baseUrl/download?query=%s"
    }
}
