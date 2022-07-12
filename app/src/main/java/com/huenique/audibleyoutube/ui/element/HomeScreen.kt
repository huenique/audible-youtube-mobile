package com.huenique.audibleyoutube.ui.element

import androidx.compose.runtime.Composable
import com.huenique.audibleyoutube.model.HomeViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.ui.element.home.HomeSearch

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
      HomeMusicLibrary()
    }
  }
}
