package com.huenique.audibleyoutube.service

import com.huenique.audibleyoutube.repository.Repository
import okhttp3.*
import java.io.IOException


class AudibleYoutubeApi {
    private var queryCount = 1
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

    fun searchVideo(query: String, repository: Repository<String>, callbackFn: () -> Any) {
        val request = Request.Builder()
            .url(search.format(query, queryCount))
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    repository.update(response.body!!.string())
                    callbackFn()
                }
            }
        })
    }

    companion object Url {
        private const val baseUrl = "https://audible-youtube.herokuapp.com"
        const val search = "$baseUrl/search?query=%s&count=%d"
        const val download = "$baseUrl/download?query=%s"
    }
}
