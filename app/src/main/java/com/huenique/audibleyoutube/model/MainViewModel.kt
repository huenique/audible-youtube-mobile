package com.huenique.audibleyoutube.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import com.huenique.audibleyoutube.state.*
import java.util.*

class MainViewModel : ViewModel() {
  private val _searchWidgetState: MutableState<SearchWidgetState> =
      mutableStateOf(value = SearchWidgetState.CLOSED)
  val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

  private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
  val searchTextState: State<String> = _searchTextState

  private val _httpResponseRepositoryState: MutableState<HttpResponseRepositoryState> =
      mutableStateOf(value = HttpResponseRepositoryState.DISPLAYED)
  val httpResponseRepositoryState: State<HttpResponseRepositoryState> = _httpResponseRepositoryState

  var moreActionState: SnapshotStateMap<String, String> = mutableStateMapOf()

  private val _actionRepositoryState: MutableState<ActionRepositoryState> =
      mutableStateOf(value = ActionRepositoryState.CLOSED)
  val actionRepositoryState: State<ActionRepositoryState> = _actionRepositoryState

  private val _isLoading: MutableState<Boolean> = mutableStateOf(value = false)
  val isLoading: State<Boolean> = _isLoading

  private val _playlistState: MutableState<PlaylistState> =
      mutableStateOf(value = PlaylistState.CLOSED)
  val playlistState: State<PlaylistState> = _playlistState

  private val _screenNavigationState: MutableState<ScreenNavigationState> =
      mutableStateOf(value = ScreenNavigationState.HOME)
  val screenNavigationState: State<ScreenNavigationState> = _screenNavigationState

  private val _successResponseState: MutableState<Boolean> = mutableStateOf(value = true)
  val successResponseState: State<Boolean> = _successResponseState

  private val _currentSongPlaying: MutableState<String> = mutableStateOf(value = "")
  val currentSongPlaying: State<String> = _currentSongPlaying

  private val _currentPlaylist: MutableState<String> = mutableStateOf(value = "")
  val currentPlaylist: State<String> = _currentPlaylist

  private val _playButtonState: MutableState<PlayButtonState> =
      mutableStateOf(value = PlayButtonState.PAUSED)
  val playButtonState: State<PlayButtonState> = _playButtonState

  private val _currentPlaylistContent: MutableState<TreeMap<String, String>> =
      mutableStateOf(value = TreeMap())
  val currentPlaylistContent = _currentPlaylistContent

  private val _currentSongCover: MutableState<String> = mutableStateOf(value = "")
  val currentSongCover: State<String> = _currentSongCover

  private val _currentSongDuration: MutableState<Float> = mutableStateOf(value = 0f)
  val currentSongDuration: State<Float> = _currentSongDuration

  fun updateSearchWidgetState(newValue: SearchWidgetState) {
    _searchWidgetState.value = newValue
  }

  fun updateSearchTextState(newValue: String) {
    _searchTextState.value = newValue
  }

  fun updateSearchRepoState(newValue: HttpResponseRepositoryState) {
    _httpResponseRepositoryState.value = newValue
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

  fun updateScreenNavState(newValue: ScreenNavigationState) {
    _screenNavigationState.value = newValue
  }

  fun updateSuccessResponseState(newValue: Boolean) {
    _successResponseState.value = newValue
  }

  fun updateCurrentSongPlaying(newValue: String) {
    _currentSongPlaying.value = newValue
  }

  fun updateCurrentPlaylist(newValue: String) {
    _currentPlaylist.value = newValue
  }

  fun updatePlayButtonState(newValue: PlayButtonState) {
    _playButtonState.value = newValue
  }

  fun updateCurrentPlaylistContent(newValue: TreeMap<String, String>) {
    _currentPlaylistContent.value = newValue
  }

  fun updateCurrentSongCover(newValue: String) {
    _currentSongCover.value = newValue
  }

  fun updateCurrentSongDuration(newValue: Float) {
    _currentSongDuration.value = newValue
  }
}
