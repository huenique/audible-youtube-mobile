package com.huenique.audibleyoutube.ui.element

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.SearchRepositoryState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.ui.component.VariableTopAppBar

@Composable
fun MainTopAppBar(
    mainViewModel: MainViewModel,
    searchResultRepository: SearchResultRepository,
    searchWidgetState: SearchWidgetState
) {
  val audibleYoutube = AudibleYoutubeApi()

  val searchTextState by mainViewModel.searchTextState
  val playlistState by mainViewModel.playlistState

  VariableTopAppBar(
      searchWidgetState = searchWidgetState,
      searchTextState = searchTextState,
      onTextChange = { mainViewModel.updateSearchTextState(newValue = it) },
      onCloseClicked = {
        mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
      },
      onSearchClicked = {
        mainViewModel.updateSearchRepoState(newValue = SearchRepositoryState.DISPLAYED)
        mainViewModel.updatePreloadState(newValue = true)
        audibleYoutube.searchVideo(
            query = it,
            responseRepo = searchResultRepository,
            callbackFn = {
              mainViewModel.updateSearchRepoState(newValue = SearchRepositoryState.CHANGED)
            })
      },
      onSearchTriggered = {
        mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
      })
}
