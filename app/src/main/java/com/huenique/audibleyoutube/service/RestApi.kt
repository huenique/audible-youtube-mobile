package com.huenique.audibleyoutube.service

import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.state.RepositoryState
import okhttp3.*
import java.io.IOException


class AudibleYoutubeApi {
    private var querySize = 5
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

    fun searchVideo(query: String, searchResultRepository: SearchResultRepository, viewModel: MainViewModel) {
        val request = Request.Builder()
            .url(search.format(query, querySize))
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    searchResultRepository.update(response.body!!.string())
                    viewModel.updateRepositoryState(newValue = RepositoryState.CHANGED)
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
