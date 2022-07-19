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
import java.io.File

@Composable
fun MainVideoSearch(
    viewModel: MainViewModel,
    httpResponseRepository: HttpResponseRepository,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
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
      Toast.makeText(context, "The selected video is too long!", Toast.LENGTH_SHORT).show()
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
      onMoreActionClicked = {
        viewModel.updatePlaylistState(newValue = PlaylistState.PENDING)
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.OPENED)
      },
      onAddToPlaylist = { query: String, mediaSource: File, playlist: File ->
        viewModel.updateActionRepoState(newValue = ActionRepositoryState.CLOSED)
        audibleYoutube.downloadVideo(
            query = query,
            file = mediaSource,
            responseRepo = httpResponseRepository,
            onFailure = {
              httpResponseHandler.onHttpError(
                  viewModel, httpResponseRepoState, httpResponseRepository)
            },
            onResponseFailure = { viewModel.updateSuccessResponseState(newValue = false) },
            context = context,
            builder = builder,
            onSinkClose = { musicLibraryManager.addMusicToLibrary(playlist, mediaSource) })

        val thumbnailUrl = moreActionState["thumbnail"]
        val thumbnailFile = moreActionState["videoTitle"]

        if (thumbnailUrl != null && thumbnailFile != null) {
          audibleYoutube.downloadThumbnail(
              thumbnailUrl = thumbnailUrl,
              file =
                  File(
                      context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                      "$thumbnailFile.jpg"),
              responseRepo = httpResponseRepository,
              callbackFn = {},
          )
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
        audibleYoutube.downloadVideo(
            query = query,
            file = mediaSource,
            responseRepo = httpResponseRepository,
            onFailure = {
              httpResponseHandler.onHttpError(
                  viewModel, httpResponseRepoState, httpResponseRepository)
            },
            onResponseFailure = { viewModel.updateSuccessResponseState(newValue = false) },
            context = context,
            builder = builder,
            onSinkClose = { musicLibraryManager.addMusicToLibrary(musicLibrary, mediaSource) })

        val thumbnailUrl = moreActionState["thumbnail"]
        val thumbnailFile = moreActionState["videoTitle"]

        println("thumbnailUrl: $thumbnailUrl\nthumbnailFile: $thumbnailFile")

        if (thumbnailUrl != null && thumbnailFile != null) {
          audibleYoutube.downloadThumbnail(
              thumbnailUrl = thumbnailUrl,
              file =
                  File(
                      context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                      "$thumbnailFile.jpg"),
              responseRepo = httpResponseRepository,
              callbackFn = {},
          )
        }
      })
}
