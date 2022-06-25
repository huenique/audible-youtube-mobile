package com.huenique.audibleyoutube.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.huenique.audibleyoutube.state.RepositoryState
import com.huenique.audibleyoutube.state.SearchWidgetState

class MainViewModel : ViewModel() {
    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> =
        mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    private val _repositoryState: MutableState<RepositoryState> =
        mutableStateOf(value = RepositoryState.DISPLAYED)
    val repositoryState: State<RepositoryState> = _repositoryState

    private val _isLoading: MutableState<Boolean> = mutableStateOf(value = false)
    val isLoading: State<Boolean> = _isLoading

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    fun updateRepositoryState(newValue: RepositoryState) {
        _repositoryState.value = newValue
    }

    fun updatePreloadState(newValue: Boolean) {
        _isLoading.value = newValue
    }
}
