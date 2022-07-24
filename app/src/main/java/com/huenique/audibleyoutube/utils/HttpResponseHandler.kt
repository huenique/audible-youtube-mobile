package com.huenique.audibleyoutube.utils

import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.state.HttpResponseRepositoryState

class HttpResponseHandler {
  fun onHttpError(
      viewModel: MainViewModel,
      httpResponseRepoState: HttpResponseRepositoryState,
  ) {
    when (httpResponseRepoState) {
      HttpResponseRepositoryState.DISPLAYED -> {
        viewModel.updateSearchRepoState(newValue = HttpResponseRepositoryState.CHANGED)
      }
      HttpResponseRepositoryState.INTERRUPTED -> {
        viewModel.updateSearchRepoState(newValue = HttpResponseRepositoryState.DISPLAYED)
      }
      else -> {}
    }
  }
}
