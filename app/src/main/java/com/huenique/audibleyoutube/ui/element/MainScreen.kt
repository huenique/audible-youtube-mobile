package com.huenique.audibleyoutube.ui.element

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.RepositoryState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.utils.RepositoryGetter

@Composable
fun MainScreen(mainViewModel: MainViewModel, repositoryGetter: RepositoryGetter) {
    val searchResultRepository = repositoryGetter.searchResultRepository()
    val audibleYoutube = AudibleYoutubeApi()
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState
    val repositoryState by mainViewModel.repositoryState

    Scaffold(
        topBar = {
            MainAppBar(
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = { mainViewModel.updateSearchTextState(newValue = it) },
                onCloseClicked = {
                    mainViewModel.updateSearchWidgetState(
                        newValue = SearchWidgetState.CLOSED
                    )
                },
                onSearchClicked = {
                    mainViewModel.updateRepositoryState(newValue = RepositoryState.DISPLAYED)
                    mainViewModel.updatePreloadState(newValue = true)
                    audibleYoutube.searchVideo(
                        query = it,
                        repository = searchResultRepository,
                        callbackFn = {
                            mainViewModel.updateRepositoryState(
                                newValue = RepositoryState.CHANGED
                            )
                        }
                    )
                },
                onSearchTriggered = {
                    mainViewModel.updateSearchWidgetState(
                        newValue = SearchWidgetState.OPENED
                    )
                }
            )
        },
        content = {
            MainAppContent(
                repository = searchResultRepository,
                repositoryState = repositoryState,
                mainViewModel = mainViewModel
            )
        }
    )
}
