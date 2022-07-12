package com.huenique.audibleyoutube.ui.element.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.PlaylistState
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
  val searchRepositoryState by viewModel.searchRepositoryState
  val playlistState by viewModel.playlistState

  MainTopAppBar(
      searchWidgetState = searchWidgetState,
      searchTextState = searchTextState,
      onTextChange = { viewModel.updateSearchTextState(newValue = it) },
      onCloseClicked = {
        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
        viewModel.updatePlaylistState(newValue = PlaylistState.CLOSED)
        viewModel.updateSpinnerState(newValue = false)

        // Reset repos to prevent memory hogging
        viewModel.updateSearchRepoState(newValue = SearchRepositoryState.INTERRUPTED)
        searchResultRepository.update(value = "{}")
      },
      onSearchClicked = {
        viewModel.updateSearchRepoState(newValue = SearchRepositoryState.DISPLAYED)
        viewModel.updateSpinnerState(newValue = true)

        audibleYoutube.searchVideo(
            query = it,
            responseRepo = searchResultRepository,
            callbackFn = {
              when (searchRepositoryState) {
                SearchRepositoryState.DISPLAYED -> {
                  viewModel.updateSearchRepoState(newValue = SearchRepositoryState.CHANGED)
                }
                SearchRepositoryState.INTERRUPTED -> {
                  // Forget search results and rest search repo state
                  searchResultRepository.update(value = "{}")
                  viewModel.updateSearchRepoState(newValue = SearchRepositoryState.DISPLAYED)
                }
                else -> {}
              }
            })
      },
      onSearchTriggered = {
        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
      })
}
