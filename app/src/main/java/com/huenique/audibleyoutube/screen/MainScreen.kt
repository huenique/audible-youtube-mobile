package com.huenique.audibleyoutube.screen

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.huenique.audibleyoutube.component.NavBar
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.screen.main.MainTopAppBar
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.ScreenNavigationState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import com.huenique.audibleyoutube.utils.NotificationManager
import com.huenique.audibleyoutube.utils.RepositoryGetter

object NavigationRoute {
  const val HOME = "home"
  const val SEARCH = "search"
  const val LIBRARY = "library"
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    notificationManager: NotificationManager
) {
  val navController = rememberNavController()
  val searchResultRepository = RepositoryGetter().searchResultRepository()
  val searchWidgetState by mainViewModel.searchWidgetState
  val screenNavigationState by mainViewModel.screenNavigationState

  Scaffold(
      topBar = {
        MainTopAppBar(
            viewModel = mainViewModel,
            searchResultRepository = searchResultRepository,
            searchWidgetState = searchWidgetState,
            screenNavigationState = screenNavigationState,
            navigationRoute = NavigationRoute)
      },
      content = {
        MainNavHost(
            navController = navController,
            mainViewModel = mainViewModel,
            searchResultRepository = searchResultRepository,
            searchWidgetState = searchWidgetState,
            onNavigate = { mainViewModel.updateScreenNavState(newValue = it) },
            audibleYoutube = audibleYoutube,
            musicLibraryManager = musicLibraryManager)
      },
      bottomBar = {
        NavBar(
            onHomeClick = { navController.navigate(NavigationRoute.HOME) },
            onSearchClick = { navController.navigate(NavigationRoute.SEARCH) },
            onLibraryClick = { navController.navigate(NavigationRoute.LIBRARY) })
      })
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    searchResultRepository: SearchResultRepository,
    searchWidgetState: SearchWidgetState,
    onNavigate: (ScreenNavigationState) -> Unit,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager
) {
  NavHost(navController = navController, startDestination = NavigationRoute.HOME) {
    composable(NavigationRoute.HOME) {
      onNavigate(ScreenNavigationState.HOME)
      HomeScreen(
          viewModel = mainViewModel,
          searchResultRepository = searchResultRepository,
          searchWidgetState = searchWidgetState,
          audibleYoutube = audibleYoutube,
          musicLibraryManager = musicLibraryManager)
    }
    composable(NavigationRoute.SEARCH) {
      onNavigate(ScreenNavigationState.SEARCH)
      SearchScreen(
          viewModel = mainViewModel,
          searchResultRepository = searchResultRepository,
          audibleYoutube = audibleYoutube,
          musicLibraryManager = musicLibraryManager)
    }
    composable(NavigationRoute.LIBRARY) {
      onNavigate(ScreenNavigationState.LIBRARY)
      LibraryScreen(
          viewModel = mainViewModel,
          searchResultRepository = searchResultRepository,
          searchWidgetState = searchWidgetState,
          audibleYoutube = audibleYoutube,
          musicLibraryManager = musicLibraryManager)
    }
  }
}
