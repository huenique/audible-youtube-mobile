package com.huenique.audibleyoutube.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import com.huenique.audibleyoutube.state.ActionRepositoryState
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.state.SearchRepositoryState
import com.huenique.audibleyoutube.state.SearchWidgetState

class MainViewModel : ViewModel() {
  private val _searchWidgetState: MutableState<SearchWidgetState> =
      mutableStateOf(value = SearchWidgetState.CLOSED)
  val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

  private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
  val searchTextState: State<String> = _searchTextState

  private val _searchRepositoryState: MutableState<SearchRepositoryState> =
      mutableStateOf(value = SearchRepositoryState.DISPLAYED)
  val searchRepositoryState: State<SearchRepositoryState> = _searchRepositoryState

  var moreActionState: SnapshotStateMap<String, String> = mutableStateMapOf()

  private val _actionRepositoryState: MutableState<ActionRepositoryState> =
      mutableStateOf(value = ActionRepositoryState.CLOSED)
  val actionRepositoryState: State<ActionRepositoryState> = _actionRepositoryState

  private val _isLoading: MutableState<Boolean> = mutableStateOf(value = false)
  val isLoading: State<Boolean> = _isLoading

  private val _playlistState: MutableState<PlaylistState> =
      mutableStateOf(value = PlaylistState.CLOSED)
  val playlistState: State<PlaylistState> = _playlistState

  fun updateSearchWidgetState(newValue: SearchWidgetState) {
    _searchWidgetState.value = newValue
  }

  fun updateSearchTextState(newValue: String) {
    _searchTextState.value = newValue
  }

  fun updateSearchRepoState(newValue: SearchRepositoryState) {
    _searchRepositoryState.value = newValue
  }

  fun updateActionRepoState(newValue: ActionRepositoryState) {
    _actionRepositoryState.value = newValue
  }

  fun updateSpinnerState(newValue: Boolean) {
    _isLoading.value = newValue
  }

  fun updatePlaylistState(newValue: PlaylistState) {
    _playlistState.value = newValue
  }
}
