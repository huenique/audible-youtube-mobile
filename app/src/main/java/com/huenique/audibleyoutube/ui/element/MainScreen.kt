package com.huenique.audibleyoutube.ui.element

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.SearchRepositoryState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.ui.component.MainAppBar
import com.huenique.audibleyoutube.utils.RepositoryGetter

@Composable
fun MainScreen(mainViewModel: MainViewModel, repositoryGetter: RepositoryGetter) {
  val audibleYoutube = AudibleYoutubeApi()

  val searchResultRepository = repositoryGetter.searchResultRepository()
  val searchWidgetState by mainViewModel.searchWidgetState
  val searchTextState by mainViewModel.searchTextState
  val playlistState by mainViewModel.playlistState

  Scaffold(
      topBar = {
        MainAppBar(
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
      },
      content = {
        when (searchWidgetState) {
          SearchWidgetState.OPENED -> {
            SearchScreen(
                mainViewModel = mainViewModel, searchResultRepository = searchResultRepository)
          }
          SearchWidgetState.CLOSED -> {
            LibraryScreen()
          }
        }
      })
}
