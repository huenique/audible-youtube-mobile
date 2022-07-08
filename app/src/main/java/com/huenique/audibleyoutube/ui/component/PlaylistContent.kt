package com.huenique.audibleyoutube.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme

@Composable
fun PlaylistContent(
    onCreatePlaylist: (Boolean) -> Unit,
    playlistState: PlaylistState,
    onAddToPlaylist: () -> Unit
) {
  when (playlistState) {
    PlaylistState.OPENED -> {
      Box(modifier = Modifier.fillMaxSize().background(color = Color.White)) {
        Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp)) {

          // TopBar / Content separator
          Box(modifier = Modifier.height(40.dp)) {}

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_library_add),
                contentDescription = null)

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

          // TODO: Check for existing libraries
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.ic_playlist), contentDescription = null)

            Column {
              ClickableText(
                  text = AnnotatedString("Example Playlist"),
                  modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
                  style = TextStyle(fontSize = 20.sp),
                  onClick = { onAddToPlaylist() })
              Divider(
                  Modifier.padding(start = 14.dp),
                  color = Color.Gray.copy(alpha = 0.6f),
                  thickness = 1.dp)
            }
          }
        }
      }
    }
    else -> {}
  }
}

@Composable
fun CreatePlaylistDialogue(createPlaylistState: Boolean) {
  if (createPlaylistState) {
    Box {}
  }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PlaylistPreview() {
  val state = remember { mutableStateOf(value = true) }
  AudibleYoutubeTheme { PlaylistContent({ state.value = true }, PlaylistState.OPENED, {}) }
}
