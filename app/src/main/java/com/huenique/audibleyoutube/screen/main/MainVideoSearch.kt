package com.huenique.audibleyoutube.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.huenique.audibleyoutube.component.SearchView
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.ActionRepositoryState
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import java.io.File

@Composable
fun MainVideoSearch(
    viewModel: MainViewModel,
    searchResultRepository: SearchResultRepository,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager
) {
  val context = LocalContext.current
  val musicLibrary = musicLibraryManager.createMusicLibrary(context)
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
        viewModel.updatePlaylistState(newValue = PlaylistState.PENDING)
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.OPENED)
      },
      onAddToPlaylist = { query: String, mediaSource: File, playlist: File ->
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)
        audibleYoutube.downloadVideo(query, mediaSource) {
          musicLibraryManager.addMusicToLibrary(context, playlist, mediaSource)
        }
      },
      onCreatePlaylist = {
          externalFilesDir: File,
          playlistName: String,
          resultDialogue: MutableState<String> ->
        val isPlaylistCreated = musicLibraryManager.addPlaylist(externalFilesDir, playlistName)
        if (isPlaylistCreated) {
          resultDialogue.value = "$playlistName successfully created"
        } else {
          resultDialogue.value = "$playlistName already exists"
        }
      },
      onCloseDialogue = {
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)
      },
      onDownloadVideo = { query: String, mediaSource: File ->
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)
        audibleYoutube.downloadVideo(query, mediaSource) {
          musicLibraryManager.addMusicToLibrary(context, musicLibrary, mediaSource)
        }
      },
      onPlaylistShow = { viewModel.updatePlaylistState(newValue = PlaylistState.OPENED) })
}
