package com.huenique.audibleyoutube.component

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme
import java.io.File

// TODO: Impl higher level fn than PlaylistContent

@Composable
fun PlaylistSelection(
    playlistState: PlaylistState,
    onCreatePlaylist: ((Boolean) -> Unit)? = null,
    onAddToPlaylist: (() -> Unit)? = null,
    addToPlaylist: Boolean = false
) {
  when (playlistState) {
    PlaylistState.OPENED -> {
      if (onCreatePlaylist != null && onAddToPlaylist != null) {
        PlaylistMenu(onCreatePlaylist = onCreatePlaylist, onAddToPlaylist = onAddToPlaylist)
      }
    }
    else -> {}
  }
}

@Composable
fun PlaylistMenu(onCreatePlaylist: (Boolean) -> Unit, onAddToPlaylist: () -> Unit) {
  Box(modifier = Modifier.fillMaxSize().background(color = Color.White)) {
    Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp)) {
      Box(modifier = Modifier.height(40.dp)) {}

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.ic_library_add), contentDescription = null)

        Column {
          ClickableText(
              text = AnnotatedString("Create New"),
              modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
              style = TextStyle(fontSize = 20.sp),
              onClick = { onCreatePlaylist(true) })
          Divider(
              Modifier.padding(start = 14.dp),
              color = Color.Gray.copy(alpha = 0.6f),
              thickness = 1.dp)
        }
      }

      LocalContext.current.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath?.let { it
        ->
        File(it).walk().forEach {
          if (it.isFile) {
            Playlist(it.nameWithoutExtension, onAddToPlaylist)
          }
        }
      }
    }
  }
}

@Composable
fun Playlist(name: String, onAddToPlaylist: () -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(painter = painterResource(id = R.drawable.ic_playlist), contentDescription = null)

    Column {
      ClickableText(
          text = AnnotatedString(name),
          modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
          style = TextStyle(fontSize = 20.sp),
          onClick = { onAddToPlaylist() })
      Divider(
          Modifier.padding(start = 14.dp), color = Color.Gray.copy(alpha = 0.6f), thickness = 1.dp)
    }
  }
}

@Composable
fun CreatePlaylistDialogue(onCreateDxClose: (Boolean) -> Unit, createPlaylistState: Boolean) {
  if (createPlaylistState) {
    val playlistNameState = remember { mutableStateOf(TextFieldValue()) }
    val onPressOk = remember { mutableStateOf(value = "") }
    val playlistName = playlistNameState.value.text
    val file =
        File(
            LocalContext.current.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            "$playlistName.m3u")

    BoxWithConstraints(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
      Box(
          modifier =
              Modifier.padding(start = 18.dp, end = 18.dp, bottom = 128.dp)
                  .background(color = Color.DarkGray)
                  .size(maxHeight, height = maxHeight / 4),
          contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(
                text = "Create new",
                modifier = Modifier.padding(start = 14.dp, top = 8.dp),
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Start,
            )
          }
          TextField(
              value = playlistNameState.value,
              onValueChange = { playlistNameState.value = it },
              modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp),
              colors =
                  TextFieldDefaults.textFieldColors(
                      textColor = Color.White,
                      backgroundColor = Color.DarkGray,
                      focusedIndicatorColor = Color.White,
                      unfocusedIndicatorColor = Color.White,
                  ))

          Spacer(modifier = Modifier.weight(1f))

          Row(modifier = Modifier.height(IntrinsicSize.Min).padding(bottom = 26.dp)) {
            ClickableText(
                text = AnnotatedString(text = "Cancel"),
                modifier = Modifier.fillMaxWidth().weight(1f),
                style =
                    TextStyle(
                        color = MaterialTheme.colors.secondary,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center),
                onClick = { onCreateDxClose(false) })
            Divider(color = Color.Gray, modifier = Modifier.fillMaxHeight().width(1.dp))
            ClickableText(
                text = AnnotatedString(text = "OK"),
                modifier = Modifier.fillMaxWidth().weight(1f),
                style =
                    TextStyle(
                        color = MaterialTheme.colors.secondary,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center),
                onClick = {
                  val isPlaylistCreated = file.createNewFile()

                  if (isPlaylistCreated) {
                    onPressOk.value = "$playlistName successfully created"
                  } else {
                    onPressOk.value = "$playlistName already exists"
                  }

                  println(onPressOk.value)
                })
          }
        }
      }
    }
  }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PlaylistContentPreview() {
  val state = remember { mutableStateOf(value = true) }
  AudibleYoutubeTheme {
    PlaylistSelection(
        playlistState = PlaylistState.OPENED,
        onCreatePlaylist = { state.value = true },
        onAddToPlaylist = {})
  }
}

@Preview
@Composable
fun PlaylistPreview() {
  val state = remember { mutableStateOf(value = true) }
  AudibleYoutubeTheme { CreatePlaylistDialogue({ state.value = it }, createPlaylistState = true) }
}