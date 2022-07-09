package com.huenique.audibleyoutube.ui.element

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.utils.RepositoryGetter

@Composable
fun MainScreen(mainViewModel: MainViewModel, repositoryGetter: RepositoryGetter) {
  val searchResultRepository = repositoryGetter.searchResultRepository()
  val searchWidgetState by mainViewModel.searchWidgetState

  Scaffold(
      topBar = {
        AppBar(
            mainViewModel = mainViewModel,
            searchResultRepository = searchResultRepository,
            searchWidgetState = searchWidgetState)
      },
      content = {
        when (searchWidgetState) {
          SearchWidgetState.OPENED -> {
            Search(mainViewModel = mainViewModel, searchResultRepository = searchResultRepository)
          }
          SearchWidgetState.CLOSED -> {
            MusicLibrary()
          }
        }
      })
}
