package com.huenique.audibleyoutube.ui.element

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.ActionRepositoryState
import com.huenique.audibleyoutube.state.SearchRepositoryState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.utils.RepositoryClassMethod
import java.io.File

@Composable
fun MainScreen(mainViewModel: MainViewModel, repositoryClassMethod: RepositoryClassMethod) {
  val audibleYoutube = AudibleYoutubeApi()

  val searchResultRepository = repositoryClassMethod.searchResultRepository()
  val searchWidgetState by mainViewModel.searchWidgetState
  val searchTextState by mainViewModel.searchTextState
  val searchRepositoryState by mainViewModel.searchRepositoryState

  val moreActionState = mainViewModel.moreActionState
  val actionRepoState by mainViewModel.actionRepositoryState

  val isLoading by mainViewModel.isLoading

  Scaffold(
      topBar = {
        MainAppBar(
            searchWidgetState = searchWidgetState,
            searchTextState = searchTextState,
            onTextChange = { mainViewModel.updateSearchTextState(newValue = it) },
            onCloseClicked = {
              mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
            },
            onSearchClicked = {
              mainViewModel.updateSearchRepoState(newValue = SearchRepositoryState.DISPLAYED)
              mainViewModel.updatePreloadState(newValue = true)
              audibleYoutube.searchVideo(
                  query = it,
                  repository = searchResultRepository,
                  callbackFn = {
                    mainViewModel.updateSearchRepoState(newValue = SearchRepositoryState.CHANGED)
                  })
            },
            onSearchTriggered = {
              mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
            })
      },
      content = {
        MainAppContent(
            actionRepoState = actionRepoState,
            moreActionState = moreActionState,
            searchResultRepoState = searchRepositoryState,
            searchResultRepo = searchResultRepository,
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
            })
      })
}
