package com.huenique.audibleyoutube.ui.element.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.ActionRepositoryState
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.ui.component.SearchView
import java.io.File

@Composable
fun HomeSearch(viewModel: MainViewModel, searchResultRepository: SearchResultRepository) {
  val audibleYoutube = AudibleYoutubeApi()
  val moreActionState = viewModel.moreActionState
  val searchRepositoryState by viewModel.searchRepositoryState
  val actionRepoState by viewModel.actionRepositoryState
  val isLoading by viewModel.isLoading
  val playlistState by viewModel.playlistState

  SearchView(
      actionRepoState = actionRepoState,
      moreActionState = moreActionState,
      searchResultRepoState = searchRepositoryState,
      searchResultRepo = searchResultRepository,
      playlistState = playlistState,
      isLoading = isLoading,
      onContentLoad = { viewModel.updateSpinnerState(newValue = it) },
      onMoreActionClicked = {
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.OPENED)
      },
      onAddToPlaylist = { query: String, file: File ->
        audibleYoutube.downloadVideo(query, file)
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)
      },
      onCloseDialogue = {
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)
      },
      onPlaylistShow = { viewModel.updatePlaylistState(newValue = PlaylistState.OPENED) })
}
