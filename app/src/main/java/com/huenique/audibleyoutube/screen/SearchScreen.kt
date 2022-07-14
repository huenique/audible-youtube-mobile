package com.huenique.audibleyoutube.screen

import androidx.compose.runtime.Composable
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.screen.main.MainVideoSearch
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.utils.MusicLibraryManager

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    searchResultRepository: SearchResultRepository,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager
) {
  MainVideoSearch(
      viewModel = viewModel,
      searchResultRepository = searchResultRepository,
      audibleYoutube = audibleYoutube,
      musicLibraryManager = musicLibraryManager)
}
