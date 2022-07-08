package com.huenique.audibleyoutube.ui.component

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.huenique.audibleyoutube.R
import java.io.File

@Composable
fun AudioPlayer(audioFile: File) {
  val context = LocalContext.current

  // Create player instance for the specified audio
  // example: File(context.getDir("Music", MODE_PRIVATE), "audio.mp3")
  val mp: MediaPlayer = MediaPlayer.create(context, Uri.fromFile(audioFile))

  //  fun playAudio(url: String) {
  //    viewModelScope.launch {
  //      withContext(Dispatchers.IO) {
  //        mediaPlayer.reset()
  //        mediaPlayer.setDataSource(URL(url).toString())
  //        mediaPlayer.prepare()
  //        mediaPlayer.start()
  //      }
  //    }
  //  }
  // }

  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
    Image(
        painter = painterResource(id = R.drawable.ic_audio),
        contentDescription = "",
        modifier = Modifier.height(160.dp).width(160.dp).padding(32.dp).background(Color.Black))

    Row {
      IconButton(onClick = { mp.start() }, modifier = Modifier.size(35.dp)) {
        Icon(painter = painterResource(id = R.drawable.ic_play), contentDescription = null)
      }

      IconButton(onClick = { mp.pause() }, modifier = Modifier.size(35.dp)) {
        Icon(painter = painterResource(id = R.drawable.ic_pause), contentDescription = null)
      }
    }
  }
}
