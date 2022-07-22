package com.huenique.audibleyoutube.component

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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

@Composable
fun PlaylistSelection(
    playlistState: PlaylistState,
    playlistCreation: Boolean,
    onPlaylistCreation: (Boolean) -> Unit,
    onCreatePlaylist: ((Boolean) -> Unit)? = null,
    onSelectPlaylist: ((File, String) -> Unit)? = null,
) {
  when (playlistState) {
    PlaylistState.OPENED -> {
      if (onCreatePlaylist != null && onSelectPlaylist != null) {
        PlaylistMenu(
            playlistCreation = playlistCreation,
            onPlaylistCreation = onPlaylistCreation,
            onCreatePlaylist = onCreatePlaylist,
            onSelectPlaylist = onSelectPlaylist)
      }
    }
    else -> {}
  }
}

@Composable
fun PlaylistMenu(
    playlistCreation: Boolean,
    onPlaylistCreation: (Boolean) -> Unit,
    onCreatePlaylist: (Boolean) -> Unit,
    onSelectPlaylist: ((File, String) -> Unit)?,
) {
  val context = LocalContext.current
  val musicDir = Environment.DIRECTORY_MUSIC
  val isSysInDark = isSystemInDarkTheme()

  Surface(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp)) {
      Box(modifier = Modifier.height(40.dp)) {}

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.ic_library_add), contentDescription = null)

        Column {
          ClickableText(
              text = AnnotatedString("Create New"),
              modifier =
                  Modifier.fillMaxWidth().padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
              style =
                  TextStyle(
                      color = if (isSysInDark) Color.White else Color.Black, fontSize = 20.sp),
              onClick = { onCreatePlaylist(true) })
          Divider(
              Modifier.padding(start = 14.dp),
              color = Color.Gray.copy(alpha = 0.6f),
              thickness = 1.dp)
        }
      }

      // TODO: Clean this?
      // Collect m3u or audio playlist files inside a specified directory
      val playlists = remember { mutableListOf<File>() }

      if (playlistCreation) {
        playlists.clear()
        context.getExternalFilesDir(musicDir)?.absolutePath?.let { it ->
          File(it).walk().forEach {
            if (it.extension == "m3u" && it.nameWithoutExtension != "music_library") {
              playlists.add(it)
            }
          }
        }
      }

      playlists.forEach { PlaylistOption(playlist = it, onSelectPlaylist = onSelectPlaylist) }
      onPlaylistCreation(false)
    }
  }
}

@Composable
fun PlaylistOption(playlist: File, onSelectPlaylist: ((File, String) -> Unit)? = null) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(painter = painterResource(id = R.drawable.ic_playlist), contentDescription = null)

    Column {
      ClickableText(
          text = AnnotatedString(playlist.nameWithoutExtension),
          modifier = Modifier.fillMaxWidth().padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
          style =
              TextStyle(
                  if (isSystemInDarkTheme()) Color.White else Color.Black, fontSize = 20.sp)) {
        if (onSelectPlaylist != null) {
          onSelectPlaylist(playlist, playlist.nameWithoutExtension)
        }
      }
      Divider(
          Modifier.padding(start = 14.dp), color = Color.Gray.copy(alpha = 0.6f), thickness = 1.dp)
    }
  }
}

@Composable
fun CreatePlaylistDialogue(
    onCreatePlaylist: (File, String, MutableState<String>) -> Unit,
    onPlaylistCreation: (Boolean) -> Unit,
    onCreateDxClose: (Boolean) -> Unit,
    createPlaylistState: Boolean
) {
  if (createPlaylistState) {
    val context = LocalContext.current
    val playlistNameState = remember { mutableStateOf(TextFieldValue()) }
    val onPressOk = remember { mutableStateOf(value = "") }
    val playlistName = playlistNameState.value.text

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
                        textAlign = TextAlign.Center)) {
              context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.let { externalFilesDir ->
                onCreatePlaylist(externalFilesDir, playlistName, onPressOk)
              }
              onCreateDxClose(false)
              onPlaylistCreation(true)
            }
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun PlaylistPreview() {
  AudibleYoutubeTheme { PlaylistOption(File("", "")) }
}

@Preview
@Composable
fun CreatePlaylistDialoguePreview() {
  val state = remember { mutableStateOf(value = true) }
  AudibleYoutubeTheme {
    CreatePlaylistDialogue(
        onPlaylistCreation = {},
        onCreateDxClose = { state.value = it },
        createPlaylistState = true,
        onCreatePlaylist = {
            externalFilesDir: File,
            playlistName: String,
            resultDialogue: MutableState<String> ->
          println("$externalFilesDir, $playlistName, $resultDialogue")
          resultDialogue.value = "$playlistName successfully created"
          resultDialogue.value = "$playlistName already exists"
        })
  }
}
