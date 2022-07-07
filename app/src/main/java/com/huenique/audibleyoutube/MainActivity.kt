package com.huenique.audibleyoutube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.ui.element.MainScreen
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme
import com.huenique.audibleyoutube.utils.RepositoryGetter

class MainActivity : ComponentActivity() {

  private val repositoryGetter: RepositoryGetter = RepositoryGetter()
  private val mainViewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AudibleYoutubeTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          MainScreen(mainViewModel = mainViewModel, repositoryGetter = repositoryGetter)
        }
      }
    }
  }
}
