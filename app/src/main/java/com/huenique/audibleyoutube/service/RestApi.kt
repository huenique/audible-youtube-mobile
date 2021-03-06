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
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class AudibleYoutubeApi {
  private val queryCount = 1

  fun downloadThumbnail(
      thumbnailUrl: String,
      file: File,
      responseRepo: Repository<String>,
      onError: () -> Unit
  ) {
    val client = OkHttpClient.Builder().build()
    val request = Request.Builder().url(thumbnailUrl).build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                e.message?.let { errMSg ->
                  responseRepo.updateError(value = errMSg.replaceFirstChar { it.uppercaseChar() })
                  onError()
                }
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) {
                    val errMessage =
                        try {
                          val jsonContent = response.body?.string()?.let { it1 -> JSONObject(it1) }
                          val reason =
                              if (jsonContent !== null)
                                  try {
                                    jsonContent.getJSONArray("errors").get(0) as String
                                  } catch (e: JSONException) {
                                    jsonContent.getString("error")
                                  }
                              else ""
                          reason.replaceFirstChar { it.uppercaseChar() }
                        } catch (jsonErr: JSONException) {
                          response.message
                        }

                    responseRepo.updateError(value = errMessage)
                    onError()
                  } else {
                    val inputStream = response.body!!.byteStream()
                    val buffer = ByteArray(1024 * 4)
                    val off = 0
                    var len = 0

                    try {
                      val fos = FileOutputStream(file)
                      while (inputStream.read(buffer).apply { len = this } > 0) {
                        fos.write(buffer, off, len)
                      }
                      fos.flush()
                      fos.close()
                    } catch (e: IOException) {
                      e.message?.let { ioErr -> responseRepo.updateError(value = ioErr) }
                    }
                  }
                }
              }
            })
  }

  fun downloadVideo(
      query: String,
      file: File,
      responseRepo: Repository<String>,
      onFailure: () -> Unit,
      onError: () -> Unit,
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
                e.message?.let { it: String ->
                  responseRepo.updateError(value = it.replaceFirstChar { it.uppercaseChar() })
                }
                onFailure()
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  var errMessage = ""
                  if (!response.isSuccessful) {
                    errMessage =
                        try {
                          val jsonContent = response.body?.string()?.let { it1 -> JSONObject(it1) }
                          val reason =
                              if (jsonContent !== null)
                                  try {
                                    jsonContent.getJSONArray("errors").get(0) as String
                                  } catch (e: JSONException) {
                                    jsonContent.getString("error")
                                  }
                              else ""
                          reason.replaceFirstChar { it.uppercaseChar() }
                        } catch (jsonErr: JSONException) {
                          response.message
                        }

                    responseRepo.updateError(value = errMessage)
                    onError()
                  } else {
                    val respBody = response.body
                    val sourceBytes = respBody!!.source()
                    val sink: BufferedSink = file.sink().buffer()

                    // Create a unique int for the notification id.
                    val notificationId = (respBody.contentLength() + file.length()).toInt()

                    var totalRead: Long = 0
                    var lastRead: Long

                    if (context != null && builder != null) {
                      NotificationManagerCompat.from(context).apply {
                        builder
                            .setContentTitle(file.nameWithoutExtension)
                            .setOnlyAlertOnce(true)
                            .setContentText("Download in progress")
                            .setProgress(respBody.contentLength().toInt(), totalRead.toInt(), false)
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

                        // For some reason, solely using setProgress() to update the notification
                        // does not work. As a temporary workaround, we use cancel().
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
              }
            })
  }

  fun searchVideo(
      query: String,
      responseRepo: Repository<String>,
      onError: () -> Unit,
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
                  responseRepo.updateError(value = it)
                  callbackFn()
                }
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  var errMessage = ""
                  if (!response.isSuccessful) {
                    errMessage =
                        try {
                          val jsonContent = response.body?.string()?.let { it1 -> JSONObject(it1) }
                          val reason =
                              if (jsonContent !== null)
                                  try {
                                    jsonContent.getJSONArray("errors").get(0) as String
                                  } catch (e: JSONException) {
                                    jsonContent.getString("error")
                                  }
                              else ""
                          reason.replaceFirstChar { it.uppercaseChar() }
                        } catch (jsonErr: JSONException) {
                          response.message
                        }

                    responseRepo.updateError(value = errMessage)
                    onError()
                  } else {
                    responseRepo.updateContent(response.body!!.string())
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
    const val INSUFFICIENT_STORAGE = 507
  }
}
