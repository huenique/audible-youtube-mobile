package com.huenique.audibleyoutube.screen

import androidx.compose.runtime.Composable
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.screen.main.MainVideoSearch

@Composable
fun SearchScreen(viewModel: MainViewModel, searchResultRepository: SearchResultRepository) {
  MainVideoSearch(viewModel = viewModel, searchResultRepository = searchResultRepository)
}
