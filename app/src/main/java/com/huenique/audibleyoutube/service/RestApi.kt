package com.huenique.audibleyoutube.service

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.repository.Repository
import okhttp3.*
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class AudibleYoutubeApi {
  private val queryCount = 1

  fun downloadVideo(
      query: String,
      file: File,
      context: Context? = null,
      builder: NotificationCompat.Builder? = null,
      onSinkClose: (() -> Unit)? = null
  ) {
    val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build()
    val request = Request.Builder().url(download.format(query)).build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                // TODO: Handle code=507, message=Insufficient Storage
                e.printStackTrace()
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) throw IOException("Unexpected code $response")
                  val respBody = response.body
                  val sourceBytes = respBody!!.source()
                  val sink: BufferedSink = file.sink().buffer()

                  // Create a unique int for the notification id
                  val notificationId = (respBody.contentLength() + file.length()).toInt()

                  var totalRead: Long = 0
                  var lastRead: Long

                  if (context != null && builder != null) {
                    NotificationManagerCompat.from(context).apply {
                      builder.setContentTitle(file.nameWithoutExtension)
                      builder.setContentText("Download in progress")
                      builder.setProgress(
                          respBody.contentLength().toInt(), totalRead.toInt(), false)
                      notify(notificationId, builder.build())

                      while (sourceBytes.read(sink.buffer, 8L * 1024).also { lastRead = it } !=
                          -1L) {
                        totalRead += lastRead
                        sink.emitCompleteSegments()

                        val contentLength = respBody.contentLength().toInt()
                        val totalLength = totalRead.toInt()

                        builder.setProgress(contentLength, totalLength, false)
                        notify(notificationId, builder.build())
                      }

                      builder
                          .setSmallIcon(R.drawable.ic_baseline_check)
                          .setContentText("Download complete")
                          .setProgress(0, 0, false)

                      // For some reason, solely using setProgress() to update the notification does
                      // not work. As a temporary workaround, we use cancel().
                      cancel(notificationId)
                      notify(notificationId, builder.build())
                    }
                  }

                  sink.writeAll(sourceBytes)
                  sink.close()
                  if (onSinkClose != null) {
                    onSinkClose()
                  }
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
