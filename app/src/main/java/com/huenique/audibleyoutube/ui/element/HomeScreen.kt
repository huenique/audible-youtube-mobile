package com.huenique.audibleyoutube.ui.element

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.ui.component.NavBar
import com.huenique.audibleyoutube.ui.element.home.HomeSearch
import com.huenique.audibleyoutube.ui.element.home.HomeTopAppBar
import com.huenique.audibleyoutube.utils.RepositoryGetter

@Composable
fun HomeScreen(
    onNavigation: () -> Unit,
    viewModel: MainViewModel,
    repositoryGetter: RepositoryGetter
) {
  val searchResultRepository = repositoryGetter.searchResultRepository()
  val searchWidgetState by viewModel.searchWidgetState

  Scaffold(
      topBar = {
        HomeTopAppBar(
            viewModel = viewModel,
            searchResultRepository = searchResultRepository,
            searchWidgetState = searchWidgetState)
      },
      content = {
        when (searchWidgetState) {
          SearchWidgetState.OPENED -> {
            HomeSearch(viewModel = viewModel, searchResultRepository = searchResultRepository)
          }
          SearchWidgetState.CLOSED -> {
            HomeMusicLibrary()
          }
        }
      },
      bottomBar = { NavBar() })
}
