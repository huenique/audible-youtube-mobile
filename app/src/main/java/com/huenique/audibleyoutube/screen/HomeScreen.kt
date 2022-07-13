package com.huenique.audibleyoutube.screen

import androidx.compose.runtime.Composable
import com.huenique.audibleyoutube.model.HomeViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.screen.home.HomeSearch
import com.huenique.audibleyoutube.state.SearchWidgetState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    searchResultRepository: SearchResultRepository,
    searchWidgetState: SearchWidgetState
) {

  when (searchWidgetState) {
    SearchWidgetState.OPENED -> {
      HomeSearch(viewModel = viewModel, searchResultRepository = searchResultRepository)
    }
    SearchWidgetState.CLOSED -> {
      HomeSelection()
    }
  }
}
