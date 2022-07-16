package com.huenique.audibleyoutube.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.screen.main.MainVideoSearch
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import com.huenique.audibleyoutube.utils.NotificationManager

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    searchResultRepository: SearchResultRepository,
    searchWidgetState: SearchWidgetState,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    notificationManager: NotificationManager
) {
  when (searchWidgetState) {
    SearchWidgetState.OPENED -> {
      MainVideoSearch(
          viewModel = viewModel,
          searchResultRepository = searchResultRepository,
          audibleYoutube = audibleYoutube,
          musicLibraryManager = musicLibraryManager,
          notificationManager = notificationManager)
    }
    SearchWidgetState.CLOSED -> {
      HomeSelection()
    }
  }
}

@Composable
fun HomeSelection() {
  Column {
    RecentlyPlayed()
    RecentlyAdded()
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecentlyPlayed() {
  Text(
      text = "Recently Played",
      modifier = Modifier.padding(start = 14.dp, top = 24.dp),
      color = if (isSystemInDarkTheme()) Color.White else Color.Black)
  HorizontalPager(count = 5) {
    Image(painter = painterResource(id = R.drawable.placeholder_image), contentDescription = null)
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecentlyAdded() {
  Text(
      text = "Recently Added",
      modifier = Modifier.padding(start = 14.dp, top = 24.dp),
      color = if (isSystemInDarkTheme()) Color.White else Color.Black)
  HorizontalPager(count = 5) {
    Image(painter = painterResource(id = R.drawable.placeholder_image), contentDescription = null)
  }
}

@Preview
@Composable
fun MusicLibraryPreview() {
  AudibleYoutubeTheme { HomeSelection() }
}
