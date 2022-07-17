package com.huenique.audibleyoutube.component

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.huenique.audibleyoutube.R

@Composable
fun AudioPlayer(mediaPlayer: MediaPlayer) {
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
    Image(
        painter = painterResource(id = R.drawable.ic_audio),
        contentDescription = "",
        modifier = Modifier.height(160.dp).width(160.dp).padding(32.dp).background(Color.Black))

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
