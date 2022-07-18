package com.huenique.audibleyoutube.screen.main

import androidx.compose.runtime.*
import com.huenique.audibleyoutube.component.CreatePlaylistDialogue
import com.huenique.audibleyoutube.component.PlaylistSelection
import com.huenique.audibleyoutube.state.PlaylistState
import java.io.File

@Composable
fun MainPlaylistSelection(
    playlistState: PlaylistState,
    onCreatePlaylist: (File, String, MutableState<String>) -> Unit,
    onSelectPlaylist: (File, String) -> Unit
) {
  val createPlaylistDxState = remember { mutableStateOf(value = false) }
  val playlistCreationState = remember { mutableStateOf(value = true) }
  val playlistCreation by playlistCreationState

  PlaylistSelection(
      playlistState = playlistState,
      playlistCreation = playlistCreation,
      onPlaylistCreation = { playlistCreationState.value = it },
      onCreatePlaylist = { createPlaylistDxState.value = it },
      onSelectPlaylist = onSelectPlaylist)
  CreatePlaylistDialogue(
      onCreatePlaylist = onCreatePlaylist,
      onPlaylistCreation = { playlistCreationState.value = it },
      onCreateDxClose = { createPlaylistDxState.value = it },
      createPlaylistState = createPlaylistDxState.value)
}
