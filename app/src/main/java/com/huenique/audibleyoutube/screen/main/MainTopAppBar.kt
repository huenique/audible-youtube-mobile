package com.huenique.audibleyoutube.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.component.TopBar
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.screen.NavigationRoute
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.state.ScreenNavigationState
import com.huenique.audibleyoutube.state.SearchRepositoryState
import com.huenique.audibleyoutube.state.SearchWidgetState

@Composable
fun MainTopAppBar(
    viewModel: MainViewModel,
    searchResultRepository: SearchResultRepository,
    searchWidgetState: SearchWidgetState,
    screenNavigationState: ScreenNavigationState,
    navigationRoute: NavigationRoute
) {
  val audibleYoutube = AudibleYoutubeApi()
  val searchTextState by viewModel.searchTextState
  val searchRepositoryState by viewModel.searchRepositoryState
  val topBarTitle =
      (when (screenNavigationState) {
            ScreenNavigationState.HOME -> {
              navigationRoute.HOME
            }
            ScreenNavigationState.SEARCH -> {
              navigationRoute.SEARCH
            }
            ScreenNavigationState.LIBRARY -> {
              navigationRoute.LIBRARY
            }
          })
          .replaceFirstChar { it.titlecase() }

  TopBar(
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
      },
      title = topBarTitle)
}
