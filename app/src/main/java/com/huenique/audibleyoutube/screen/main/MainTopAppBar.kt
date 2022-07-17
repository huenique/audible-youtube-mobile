package com.huenique.audibleyoutube.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.component.TopBar
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.screen.NavigationRoute
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.HttpResponseRepositoryState
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.state.ScreenNavigationState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.utils.HttpResponseHandler

@Composable
fun MainTopAppBar(
    viewModel: MainViewModel,
    httpResponseRepository: HttpResponseRepository,
    searchWidgetState: SearchWidgetState,
    screenNavigationState: ScreenNavigationState,
    navigationRoute: NavigationRoute,
    httpResponseHandler: HttpResponseHandler
) {
  val audibleYoutube = AudibleYoutubeApi()
  val searchTextState by viewModel.searchTextState
  val httpResponseRepoState by viewModel.httpResponseRepositoryState
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
            else -> {
              ""
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
        viewModel.updateSearchRepoState(newValue = HttpResponseRepositoryState.INTERRUPTED)
        httpResponseRepository.update(value = "{}")
      },
      onSearchClicked = {
        viewModel.updateSearchRepoState(newValue = HttpResponseRepositoryState.DISPLAYED)
        viewModel.updateSpinnerState(newValue = true)

        audibleYoutube.searchVideo(
            query = it,
            responseRepo = httpResponseRepository,
            callbackFn = {
              httpResponseHandler.onHttpError(
                  viewModel, httpResponseRepoState, httpResponseRepository)
            })
      },
      onSearchTriggered = {
        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
      },
      title = topBarTitle)
}
