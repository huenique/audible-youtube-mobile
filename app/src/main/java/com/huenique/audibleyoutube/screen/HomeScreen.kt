package com.huenique.audibleyoutube.screen

import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.screen.main.MainVideoSearch
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.PlayButtonState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.utils.HttpResponseHandler
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import com.huenique.audibleyoutube.utils.RecentManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: MainViewModel,
    mediaPlayer: MediaPlayer,
    musicLibraryManager: MusicLibraryManager,
    httpResponseRepository: HttpResponseRepository,
    searchWidgetState: SearchWidgetState,
    audibleYoutube: AudibleYoutubeApi,
    recentManager: RecentManager,
    httpResponseHandler: HttpResponseHandler
) {
  val context = LocalContext.current

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
      val recentAddedSongs = recentManager.getRecentlyAdded(context)
      val recentPlayedSongs = recentManager.getRecentlyPlayed(context)

      HomeSelection(
          recentPlayedSongs = recentPlayedSongs,
          recentAddedSongs = recentAddedSongs,
          onThumbnailClick = { thumbnail: String ->
            val song = musicLibraryManager.getSongByThumbnail(context, thumbnail)

            recentManager.addToRecentlyPlayed(context, thumbnail)
            viewModel.updatePlayButtonState(newValue = PlayButtonState.PLAYING)
            viewModel.updateCurrentSongCover(newValue = thumbnail)
            viewModel.updateCurrentSongPlaying(newValue = song.nameWithoutExtension)
            viewModel.viewModelScope.launch {
              withContext(Dispatchers.IO) {
                try {
                  mediaPlayer.reset()
                  mediaPlayer.setDataSource(context, song.toUri())
                  mediaPlayer.prepare()
                  mediaPlayer.start()
                } catch (e: Exception) {
                  e.printStackTrace()
                }
              }
            }

            navHostController.navigate(NavigationRoute.PLAYER)
          })
    }
  }
}

@Composable
fun HomeSelection(
    recentPlayedSongs: List<String>,
    recentAddedSongs: List<String>,
    onThumbnailClick: (String) -> Unit
) {
  Column {
    RecentlyPlayed(recentPlayedSongs = recentPlayedSongs, onThumbnailClick = onThumbnailClick)
    RecentlyAdded(recentAddedSongs = recentAddedSongs, onThumbnailClick = onThumbnailClick)
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecentlyPlayed(recentPlayedSongs: List<String>, onThumbnailClick: (String) -> Unit) {
  val horizontalPadding = 34.dp
  val itemWidth = 340.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp
  val contentPadding =
      PaddingValues(
          start = horizontalPadding, end = (screenWidth.dp - itemWidth + horizontalPadding))

  Text(
      text = "Recently Played",
      modifier = Modifier.padding(start = 14.dp, top = 24.dp, bottom = 24.dp),
      color = if (isSystemInDarkTheme()) Color.White else Color.Black)
  Box(modifier = Modifier.height(128.dp), contentAlignment = Alignment.CenterStart) {
    HorizontalPager(
        count = recentPlayedSongs.size,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalAlignment = Alignment.CenterVertically) { page ->
      AsyncImage(
          model = recentPlayedSongs[page],
          contentDescription = "Recently played song cover",
          modifier =
              Modifier.clip(RoundedCornerShape(16.dp)).height(128.dp).clickable {
                onThumbnailClick(recentPlayedSongs[page])
              },
          contentScale = ContentScale.Fit)
    }
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecentlyAdded(recentAddedSongs: List<String>, onThumbnailClick: (String) -> Unit) {
  val horizontalPadding = 34.dp
  val itemWidth = 340.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp
  val contentPadding =
      PaddingValues(
          start = horizontalPadding, end = (screenWidth.dp - itemWidth + horizontalPadding))

  Text(
      text = "Recently Added",
      modifier = Modifier.padding(start = 14.dp, top = 24.dp, bottom = 24.dp),
      color = if (isSystemInDarkTheme()) Color.White else Color.Black)
  Box(modifier = Modifier.height(128.dp), contentAlignment = Alignment.CenterStart) {
    HorizontalPager(
        count = recentAddedSongs.size,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalAlignment = Alignment.CenterVertically) { page ->
      AsyncImage(
          model = recentAddedSongs[page],
          contentDescription = "Recently played song cover",
          modifier =
              Modifier.clip(RoundedCornerShape(16.dp)).height(128.dp).clickable {
                onThumbnailClick(recentAddedSongs[page])
              },
          contentScale = ContentScale.Fit)
    }
  }
}
