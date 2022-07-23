package com.huenique.audibleyoutube.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.component.MoreActionOption
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
import com.huenique.audibleyoutube.utils.RecentManager
import java.io.File

@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    navController: NavHostController,
    searchWidgetState: SearchWidgetState,
    httpResponseRepository: HttpResponseRepository,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    recentManager: RecentManager,
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
          recentManager = recentManager,
          httpResponseHandler = httpResponseHandler)
    }
    SearchWidgetState.CLOSED -> {
      LibrarySelection(
          viewModel = viewModel,
          navController = navController,
          musicLibraryManager = musicLibraryManager,
          onClickAllSongs = onClickAllSongs)
    }
  }
}

@Composable
fun LibrarySelection(
    viewModel: MainViewModel,
    navController: NavHostController,
    musicLibraryManager: MusicLibraryManager,
    onClickAllSongs: () -> Unit
) {
  val playlistState by viewModel.playlistState
  var libraryViewState by remember { mutableStateOf("libraryOptions") }

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
          playlistState = playlistState,
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
            val songs = musicLibraryManager.getSongsFromPlaylist(playlist)
            viewModel.updateCurrentPlaylistContent(newValue = songs)
            viewModel.updateCurrentPlaylist("$playlistName.m3u")
            navController.navigate(NavigationRoute.PLAYLIST)
          })
    }
  }
}

@Composable
fun Playlist(
    playButtonState: PlayButtonState,
    currentSongPlaying: String,
    songs: MutableMap<String, String>,
    onSongClick: (String, String) -> Unit,
    onDeleteSong: (String) -> Unit
) {
  var moreActionState by remember { mutableStateOf(value = false) }
  var songTitle by remember { mutableStateOf(value = "") }

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
          onClickMoreAction = {
            moreActionState = true
            songTitle = song.key
          })
    }

    Box(modifier = Modifier.height(80.dp)) {}
  }

  when (moreActionState) {
    true -> {
      Box(
          modifier =
              Modifier.fillMaxSize()
                  .background(MaterialTheme.colors.background.copy(alpha = 0.6f))
                  .clickable { moreActionState = false },
          contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier.background(Color.DarkGray)
                    .width(LocalConfiguration.current.screenWidthDp.dp / 2)) {
          Column(modifier = Modifier.padding(start = 14.dp)) {
            MoreActionOption(
                text = "Delete",
                onClick = {
                  moreActionState = false
                  onDeleteSong(songTitle)
                })
          }
        }
      }
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
    onClickMoreAction: () -> Unit
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

    IconButton(onClick = { onClickMoreAction() }) {
      Icon(
          imageVector = Icons.Rounded.MoreVert,
          modifier = Modifier.size(24.dp),
          contentDescription = "Song more action")
    }
  }
  Divider(color = Color.Gray.copy(alpha = 0.6f), thickness = 1.dp)
}
