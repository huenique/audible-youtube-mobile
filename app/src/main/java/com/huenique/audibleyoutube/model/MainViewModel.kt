package com.huenique.audibleyoutube.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.huenique.audibleyoutube.state.ScreenNavigationState

class MainViewModel : ViewModel() {
  private val _screenNavigationState: MutableState<ScreenNavigationState> =
      mutableStateOf(value = ScreenNavigationState.HOME)
  val screenNavigationState: State<ScreenNavigationState> = _screenNavigationState

  fun updateScreenNavState(newValue: ScreenNavigationState) {
    _screenNavigationState.value = newValue
  }
}
