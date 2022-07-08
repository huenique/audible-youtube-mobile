package com.huenique.audibleyoutube.ui.element

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.ActionRepositoryState
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.ui.component.SearchViewContent
import java.io.File

@Composable
fun SearchScreen(mainViewModel: MainViewModel, searchResultRepository: SearchResultRepository) {
  val audibleYoutube = AudibleYoutubeApi()
  val moreActionState = mainViewModel.moreActionState
  val searchRepositoryState by mainViewModel.searchRepositoryState
  val actionRepoState by mainViewModel.actionRepositoryState
  val isLoading by mainViewModel.isLoading
  val playlistState by mainViewModel.playlistState

  SearchViewContent(
      actionRepoState = actionRepoState,
      moreActionState = moreActionState,
      searchResultRepoState = searchRepositoryState,
      searchResultRepo = searchResultRepository,
      playlistState = playlistState,
      isLoading = isLoading,
      onContentLoad = { mainViewModel.updatePreloadState(newValue = it) },
      onMoreActionClicked = {
        mainViewModel.updateActionRepoState(newValue = ActionRepositoryState.OPENED)
      },
      onAddToPlaylist = { query: String, file: File ->
        audibleYoutube.downloadVideo(query, file)
        mainViewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)
      },
      onCloseDialogue = {
        mainViewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)
      },
      onPlaylistShow = {
        mainViewModel.updatePlaylistState(newValue = PlaylistState.OPENED)
      }
  )
}
