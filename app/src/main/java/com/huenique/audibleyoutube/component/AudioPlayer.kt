package com.huenique.audibleyoutube.component

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.huenique.audibleyoutube.R

@Composable
fun AudioPlayer(mediaPlayer: MediaPlayer, isPlayerMinimized: Boolean, onPlayerClick: () -> Unit) {

  // Get all songs in the library or playlist.
  // When the user plays a song in a different library or playlist, AudioPlayer should open the
  // entire player and mediaPlayer for that library or playlist.

  when (isPlayerMinimized) {
    true -> {
      MinimizedPlayer(onPlayerClick = onPlayerClick)
    }
    false ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
          Image(
              painter = painterResource(id = R.drawable.ic_audio),
              contentDescription = "",
              modifier =
                  Modifier.height(160.dp).width(160.dp).padding(32.dp).background(Color.Black))

          Row {
            IconButton(onClick = { mediaPlayer.start() }, modifier = Modifier.size(35.dp)) {
              Icon(painter = painterResource(id = R.drawable.ic_play), contentDescription = null)
            }

            IconButton(onClick = { mediaPlayer.pause() }, modifier = Modifier.size(35.dp)) {
              Icon(painter = painterResource(id = R.drawable.ic_pause), contentDescription = null)
            }
          }
        }
  }
}

@Composable
fun MinimizedPlayer(mediaPlayer: MediaPlayer? = null, onPlayerClick: () -> Unit = {}) {
  Surface(
      modifier = Modifier.fillMaxWidth().height(56.dp).clickable(onClick = onPlayerClick),
      elevation = AppBarDefaults.BottomAppBarElevation,
      color = MaterialTheme.colors.primaryVariant) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
      Image(
          painter = painterResource(id = R.drawable.ic_baseline_image),
          contentDescription = "Song cover",
          colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onPrimary))
      IconButton(onClick = { mediaPlayer?.start() }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_play),
            contentDescription = "Minimized player's play button",
            tint = Color.White)
      }
      IconButton(onClick = { mediaPlayer?.start() }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_skip_next),
            contentDescription = "Minimized player's play button",
            tint = Color.White)
      }
    }
  }
}

@Preview
@Composable
fun MinimizedPlayerPreview() {
  MinimizedPlayer()
}
