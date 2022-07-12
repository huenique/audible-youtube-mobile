package com.huenique.audibleyoutube.ui.element.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.SearchRepositoryState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.ui.component.MainTopAppBar

@Composable
fun HomeTopAppBar(
    viewModel: MainViewModel,
    searchResultRepository: SearchResultRepository,
    searchWidgetState: SearchWidgetState
) {
  val audibleYoutube = AudibleYoutubeApi()

  val searchTextState by viewModel.searchTextState
  val playlistState by viewModel.playlistState

  MainTopAppBar(
      searchWidgetState = searchWidgetState,
      searchTextState = searchTextState,
      onTextChange = { viewModel.updateSearchTextState(newValue = it) },
      onCloseClicked = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED) },
      onSearchClicked = {
        viewModel.updateSearchRepoState(newValue = SearchRepositoryState.DISPLAYED)
        viewModel.updatePreloadState(newValue = true)
        audibleYoutube.searchVideo(
            query = it,
            responseRepo = searchResultRepository,
            callbackFn = {
              viewModel.updateSearchRepoState(newValue = SearchRepositoryState.CHANGED)
            })
      },
      onSearchTriggered = {
        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
      })
}
