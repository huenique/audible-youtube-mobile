package com.huenique.audibleyoutube.service

import com.huenique.audibleyoutube.repository.Repository
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.*
import okio.BufferedSink
import okio.buffer
import okio.sink

class AudibleYoutubeApi {
  private val queryCount = 1

  fun downloadVideo(query: String, file: File) {
    val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build()
    val request = Request.Builder().url(download.format(query)).build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) throw IOException("Unexpected code $response")
                  val sourceBytes = response.body!!.source()
                  val sink: BufferedSink = file.sink().buffer()

                  var totalRead: Long = 0
                  var lastRead: Long

                  while (sourceBytes.read(sink.buffer, 8L * 1024).also { lastRead = it } != -1L) {
                    totalRead += lastRead
                    sink.emitCompleteSegments()

                    // Notify user of download progress
                    println(totalRead)
                  }

                  sink.writeAll(sourceBytes)
                  sink.close()
                }
              }
            })
  }

  fun searchVideo(
      query: String,
      responseRepo: Repository<String>,
      callbackFn: () -> Any,
  ) {
    val client = OkHttpClient.Builder().connectTimeout(timeout = 30, TimeUnit.SECONDS).build()
    val request = Request.Builder().url(search.format(query, queryCount)).build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                e.message?.let {
                  responseRepo.update(value = "message: $it")
                  callbackFn()
                }
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) {
                    responseRepo.update(
                        value = "code: ${response.code}\nmessage: ${response.message}")
                  } else {
                    responseRepo.update(response.body!!.string())
                  }
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
