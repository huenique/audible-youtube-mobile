package com.huenique.audibleyoutube.component

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.screen.Song
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Playlist(
    viewModel: MainViewModel,
    songs: MutableMap<Int, Map<String, String>>,
    mediaPlayer: MediaPlayer
) {
  val context = LocalContext.current

  // Adding a song to this list will prevent creating multiple playing indicators or pause icons.
  // TODO: Move this to main view model
  var currentlyPlaying by rememberSaveable { mutableStateOf(value = "") }

  Column(
      modifier =
          Modifier.padding(start = 14.dp, end = 14.dp).verticalScroll(rememberScrollState())) {
    Box(modifier = Modifier.height(40.dp)) {}

    songs.forEach { song ->
      val songDuration = song.value["songDuration"] as String
      val songTitle = song.value["songTitle"] as String
      val songPath = song.value["songPath"] as String

      Song(
          title = songTitle,
          currentlyPlaying = currentlyPlaying,
          onClick = {
            if (currentlyPlaying.isNotEmpty()) {
              currentlyPlaying = ""
            }
            currentlyPlaying = songTitle

            // TODO: We should instead invoke AudioPlayer
            viewModel.viewModelScope.launch {
              withContext(Dispatchers.IO) {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(context, File(songPath).toUri())
                mediaPlayer.prepare()
                mediaPlayer.start()
              }
            }
          },
          onMoreActionClicked = {})
    }
  }
}
