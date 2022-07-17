package com.huenique.audibleyoutube

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.screen.MainScreen
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme
import com.huenique.audibleyoutube.utils.HttpResponseHandler
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import com.huenique.audibleyoutube.utils.NotificationManager

class MainActivity : ComponentActivity() {
  private val mainModel: MainViewModel by viewModels()
  private val audibleYoutube = AudibleYoutubeApi()
  private val musicLibraryManager = MusicLibraryManager()
  private val notificationManager = NotificationManager()
  private val httpResponseHandler = HttpResponseHandler()
  private val mediaPlayer = MediaPlayer()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      AudibleYoutubeTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          MainScreen(
              mainModel,
              audibleYoutube,
              musicLibraryManager,
              notificationManager,
              httpResponseHandler,
              mediaPlayer)
        }
      }
    }
  }
}

// TODO: use dependency injection
