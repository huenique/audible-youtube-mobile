package com.huenique.audibleyoutube.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.screen.main.MainVideoSearch
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme
import com.huenique.audibleyoutube.utils.HttpResponseHandler
import com.huenique.audibleyoutube.utils.MusicLibraryManager

@Composable
fun LibraryScreen(
    searchWidgetState: SearchWidgetState,
    viewModel: MainViewModel,
    httpResponseRepository: HttpResponseRepository,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    httpResponseHandler: HttpResponseHandler
) {
  when (searchWidgetState) {
    SearchWidgetState.OPENED -> {
      MainVideoSearch(
          viewModel = viewModel,
          httpResponseRepository = httpResponseRepository,
          audibleYoutube = audibleYoutube,
          musicLibraryManager = musicLibraryManager,
          httpResponseHandler = httpResponseHandler)
    }
    SearchWidgetState.CLOSED -> {
      var librarySelectionState by remember { mutableStateOf(value = 0) }

      when (librarySelectionState) {
        1 -> BackHandler(enabled = true) { librarySelectionState = 0 }
      }

      when (librarySelectionState) {
        0 -> LibrarySelection(onAllSongsClick = { librarySelectionState = 1 })
        1 -> {
          val songs = musicLibraryManager.getAllSongs(LocalContext.current)
          AllSongs(songs = songs)
        }
      }
    }
  }
}

@Composable
fun LibrarySelection(onAllSongsClick: () -> Unit) {
  Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp)) {
    Box(modifier = Modifier.height(40.dp)) {}
    LibraryOption(
        title = "All Songs", resourceId = R.drawable.ic_library_music, onClick = onAllSongsClick)
    LibraryOption(title = "Playlists", resourceId = R.drawable.ic_playlist, onClick = {})
  }
}

@Composable
fun AllSongs(songs: MutableMap<String, String>) {
  // Adding a song to this list will prevent creating multiple playing indicators or pause icons.
  var currentlyPlaying by remember { mutableStateOf(value = "") }

  Column(
      modifier =
          Modifier.padding(start = 14.dp, end = 14.dp).verticalScroll(rememberScrollState())) {
    Box(modifier = Modifier.height(40.dp)) {}
    for (key in songs.keys) {
      Song(
          title = key,
          currentlyPlaying = currentlyPlaying,
          onClick = {
            // TODO: temp solution to hide pause icon/playing indicator
            // The media player should pop up
            if (currentlyPlaying.isNotEmpty()) {
              currentlyPlaying = ""
            }

            currentlyPlaying = key
          },
          onMoreActionClicked = {})
    }
  }
}

@Composable
fun LibraryOption(title: String, resourceId: Int, onClick: () -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(painter = painterResource(id = resourceId), contentDescription = null)

    Column {
      ClickableText(
          text = AnnotatedString(title),
          modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp).fillMaxWidth(),
          style =
              TextStyle(
                  color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                  fontSize = 20.sp),
          onClick = { onClick() })
      Divider(
          modifier = Modifier.padding(start = 14.dp),
          color = Color.Gray.copy(alpha = 0.6f),
          thickness = 1.dp)
    }
  }
}

@Composable
fun Song(
    title: String,
    currentlyPlaying: String,
    onClick: () -> Unit,
    onMoreActionClicked: () -> Unit
) {
  Row(
      modifier =
          Modifier.clickable(
                  onClick = {
                    onClick()
                    println(currentlyPlaying)
                  })
              .padding(top = 10.dp, bottom = 10.dp)
              .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically) {
    Text(
        text = title,
        modifier = Modifier.weight(1f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1)

    when (currentlyPlaying) {
      title -> {
        Icon(
            painter = painterResource(R.drawable.ic_pause),
            modifier = Modifier.size(24.dp),
            contentDescription = "Playing indication")
      }
    }

    IconButton(onClick = { onMoreActionClicked() }) {
      Icon(
          imageVector = Icons.Rounded.MoreVert,
          modifier = Modifier.size(24.dp),
          contentDescription = "Song more action")
    }
  }
  Divider(color = Color.Gray.copy(alpha = 0.6f), thickness = 1.dp)
}

@Preview
@Composable
fun LibrarySelectionPreview() {
  LibrarySelection {}
}

@Preview
@Composable
fun SongPreview() {
  //  AudibleYoutubeTheme { Song("Song title", {}, {}) }
}

@Preview(showSystemUi = true)
@Composable
fun AllSongsPreview() {
  val songs = mutableMapOf<String, String>()
  for (i in 0..5) {
    songs[
        "Lorem ipsum dolor sit amet," +
            "consectetur adipiscing elit," +
            "sed do eiusmod tempor incididunt" +
            "ut labore et dolore magna aliqua. $i"] = "path/to/song"
  }
  AudibleYoutubeTheme { AllSongs(songs) }
}
