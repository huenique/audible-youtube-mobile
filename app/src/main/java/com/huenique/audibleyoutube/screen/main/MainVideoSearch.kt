package com.huenique.audibleyoutube.screen.main

import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.component.SearchView
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.ActionRepositoryState
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.utils.HttpResponseHandler
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import com.huenique.audibleyoutube.utils.RecentManager
import java.io.File

@Composable
fun MainVideoSearch(
    viewModel: MainViewModel,
    httpResponseRepository: HttpResponseRepository,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    recentManager: RecentManager,
    httpResponseHandler: HttpResponseHandler
) {
  val context = LocalContext.current
  val musicLibrary = musicLibraryManager.createMusicLibrary(context)
  val moreActionState = viewModel.moreActionState

  val httpResponseRepoState by viewModel.httpResponseRepositoryState
  val actionRepoState by viewModel.actionRepositoryState
  val isLoading by viewModel.isLoading
  val playlistState by viewModel.playlistState
  val successResponseState by viewModel.successResponseState

  val builder =
      NotificationCompat.Builder(context, "AudibleYouTubeChannel").apply {
        setSmallIcon(R.drawable.ic_cloud_download)
        priority = NotificationCompat.PRIORITY_LOW
      }

  when (successResponseState) {
    false -> {
      Toast.makeText(context, httpResponseRepository.getError(), Toast.LENGTH_LONG).show()
      viewModel.updateSuccessResponseState(newValue = true)
    }
  }

  SearchView(
      actionRepoState = actionRepoState,
      moreActionState = moreActionState,
      searchResultRepoState = httpResponseRepoState,
      searchResultRepo = httpResponseRepository,
      playlistState = playlistState,
      successResponseState = successResponseState,
      isLoading = isLoading,
      onContentLoad = { viewModel.updateSpinnerState(newValue = it) },
      onClickMoreAction = {
        viewModel.updatePlaylistState(newValue = PlaylistState.PENDING)
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.OPENED)
      },
      onAddToPlaylist = { query: String, mediaSource: File, playlist: File ->
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)

        val thumbnailUrl = moreActionState["thumbnail"]
        val thumbnailFileName = moreActionState["videoTitle"]?.replace("/", "")

        if (thumbnailUrl != null && thumbnailFileName != null) {
          val thumbnailFile =
              File(
                  context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                  "$thumbnailFileName.jpg")

          audibleYoutube.downloadVideo(
              query = query,
              file = mediaSource,
              responseRepo = httpResponseRepository,
              onFailure = { httpResponseHandler.onHttpError(viewModel, httpResponseRepoState) },
              onError = { viewModel.updateSuccessResponseState(newValue = false) },
              context = context,
              builder = builder,
              onSinkClose = {
                musicLibraryManager.addMusicToLibrary(context, playlist, mediaSource, thumbnailFile)
                audibleYoutube.downloadThumbnail(
                    thumbnailUrl = thumbnailUrl,
                    file = thumbnailFile,
                    responseRepo = httpResponseRepository,
                    onError = { viewModel.updateSuccessResponseState(newValue = false) },
                )
                recentManager.addToRecentlyAdded(context = context, thumbnailFile.absolutePath)
              })
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
      onPlaylistShow = { viewModel.updatePlaylistState(newValue = PlaylistState.OPENED) },
      onDownloadVideo = { query: String, mediaSource: File ->
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)

        val thumbnailUrl = moreActionState["thumbnail"]
        val thumbnailFileName = moreActionState["videoTitle"]?.replace("/", "")

        if (thumbnailUrl != null && thumbnailFileName != null) {
          val thumbnailFile =
              File(
                  context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                  "$thumbnailFileName.jpg")

          audibleYoutube.downloadVideo(
              query = query,
              file = mediaSource,
              responseRepo = httpResponseRepository,
              onFailure = { httpResponseHandler.onHttpError(viewModel, httpResponseRepoState) },
              onError = { viewModel.updateSuccessResponseState(newValue = false) },
              context = context,
              builder = builder,
              onSinkClose = {
                musicLibraryManager.addMusicToLibrary(
                    context, musicLibrary, mediaSource, thumbnailFile)
                audibleYoutube.downloadThumbnail(
                    thumbnailUrl = thumbnailUrl,
                    file = thumbnailFile,
                    responseRepo = httpResponseRepository,
                    onError = { viewModel.updateSuccessResponseState(newValue = false) },
                )
                recentManager.addToRecentlyAdded(context = context, thumbnailFile.absolutePath)
              })
        }
      })
}
