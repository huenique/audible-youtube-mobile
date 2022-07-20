package com.huenique.audibleyoutube.screen

import androidx.compose.runtime.Composable
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.screen.main.MainVideoSearch
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.utils.HttpResponseHandler
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import com.huenique.audibleyoutube.utils.RecentManager

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    httpResponseRepository: HttpResponseRepository,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    recentManager: RecentManager,
    httpResponseHandler: HttpResponseHandler
) {
  MainVideoSearch(
      viewModel = viewModel,
      httpResponseRepository = httpResponseRepository,
      audibleYoutube = audibleYoutube,
      musicLibraryManager = musicLibraryManager,
      recentManager = recentManager,
      httpResponseHandler = httpResponseHandler)
}
