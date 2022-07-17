package com.huenique.audibleyoutube.utils

import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.state.HttpResponseRepositoryState

class HttpResponseHandler {
  fun onHttpError(
      viewModel: MainViewModel,
      httpResponseRepoState: HttpResponseRepositoryState,
      httpResponseRepository: HttpResponseRepository,
      defaultRepoContent: String = "{}"
  ) {
    when (httpResponseRepoState) {
      HttpResponseRepositoryState.DISPLAYED -> {
        viewModel.updateSearchRepoState(newValue = HttpResponseRepositoryState.CHANGED)
      }
      HttpResponseRepositoryState.INTERRUPTED -> {
        httpResponseRepository.update(value = defaultRepoContent)
        viewModel.updateSearchRepoState(newValue = HttpResponseRepositoryState.DISPLAYED)
      }
      else -> {}
    }
  }
}
