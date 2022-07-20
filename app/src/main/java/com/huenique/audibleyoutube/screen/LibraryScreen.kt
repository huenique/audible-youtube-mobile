package com.huenique.audibleyoutube.screen

import android.media.MediaPlayer
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.component.Playlist
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.screen.main.MainPlaylistSelection
import com.huenique.audibleyoutube.screen.main.MainVideoSearch
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.PlayButtonState
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.utils.HttpResponseHandler
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import java.io.File

@Composable
fun LibraryScreen(
    searchWidgetState: SearchWidgetState,
    viewModel: MainViewModel,
    playButtonState: PlayButtonState,
    httpResponseRepository: HttpResponseRepository,
    audibleYoutube: AudibleYoutubeApi,
    mediaPlayer: MediaPlayer,
    musicLibraryManager: MusicLibraryManager,
    httpResponseHandler: HttpResponseHandler,
    onClickAllSongs: () -> Unit
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
      LibrarySelection(
          viewModel = viewModel,
          playButtonState = playButtonState,
          mediaPlayer = mediaPlayer,
          musicLibraryManager = musicLibraryManager,
          onClickAllSongs = onClickAllSongs)
    }
  }
}

@Composable
fun LibrarySelection(
    viewModel: MainViewModel,
    playButtonState: PlayButtonState,
    mediaPlayer: MediaPlayer,
    musicLibraryManager: MusicLibraryManager,
    onClickAllSongs: () -> Unit
) {
  val playlistState by viewModel.playlistState
  var libraryViewState by remember { mutableStateOf("libraryOptions") }
  var listedSongs by remember { mutableMapOf<String, MutableMap<Int, Map<String, String>>>() }

  when (libraryViewState) {
    "playlistSelection" -> {
      BackHandler(enabled = true) {
        libraryViewState = "libraryOptions"

        // PlaylistSelection or MainPlaylistSelection only changes when we use PlaylistState
        viewModel.updatePlaylistState(newValue = PlaylistState.CLOSED)
      }
    }
    "playlist" -> {
      BackHandler(enabled = true) { libraryViewState = "playlistSelection" }
    }
  }

  when (libraryViewState) {
    "libraryOptions" -> {
      Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp)) {
        Box(modifier = Modifier.height(40.dp)) {}
        LibraryOption(
            title = "All songs",
            resourceId = R.drawable.ic_library_music,
            onClick = onClickAllSongs)
        LibraryOption(
            title = "Playlists",
            resourceId = R.drawable.ic_playlist,
            onClick = {
              libraryViewState = "playlistSelection"
              viewModel.updatePlaylistState(newValue = PlaylistState.OPENED)
            })
      }
    }
    "playlistSelection" -> {
      MainPlaylistSelection(
          playlistState,
          onCreatePlaylist = {
              externalFilesDir: File,
              playlistName: String,
              resultDialogue: MutableState<String> ->
            val isPlaylistCreated = musicLibraryManager.addPlaylist(externalFilesDir, playlistName)
            if (isPlaylistCreated) {
              resultDialogue.value = "$playlistName successfully created"
            } else {
              resultDialogue.value = "$playlistName already exists"
            }
          },
          onSelectPlaylist = { playlist: File, playlistName: String ->
            listedSongs = musicLibraryManager.getSongsFromPlaylist(playlist)
            viewModel.updateCurrentPlaylist(playlistName)
            libraryViewState = "playlist"
          })
    }
    "playlist" -> {
      Playlist(
          viewModel = viewModel,
          playButtonState = playButtonState,
          songs = listedSongs,
          mediaPlayer = mediaPlayer)
    }
  }
}

@Composable
fun AllSongs(
    playButtonState: PlayButtonState,
    currentSongPlaying: String,
    songs: MutableMap<String, String>,
    onSongClick: (String, String) -> Unit
) {
  Column(
      modifier =
          Modifier.padding(start = 14.dp, end = 14.dp).verticalScroll(rememberScrollState())) {
    Box(modifier = Modifier.height(40.dp)) {}

    songs.forEach { song ->
      Song(
          title = song.key,
          currentSongPlaying = currentSongPlaying,
          playButtonState = playButtonState,
          onClick = { onSongClick(song.key, song.value) },
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
    currentSongPlaying: String,
    playButtonState: PlayButtonState,
    onClick: () -> Unit,
    onMoreActionClicked: () -> Unit
) {
  Row(
      modifier =
          Modifier.clickable(onClick = { onClick() })
              .padding(top = 10.dp, bottom = 10.dp)
              .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically) {
    Text(
        text = title,
        modifier = Modifier.weight(1f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1)

    when (currentSongPlaying) {
      title -> {
        when (playButtonState) {
          PlayButtonState.PLAYING -> {
            Icon(
                painter = painterResource(R.drawable.ic_pause),
                modifier = Modifier.size(24.dp),
                contentDescription = "Playing indication")
          }
          PlayButtonState.PAUSED -> {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                modifier = Modifier.size(24.dp),
                contentDescription = "Paused indication")
          }
        }
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
