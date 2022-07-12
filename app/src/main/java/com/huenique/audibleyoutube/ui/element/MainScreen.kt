package com.huenique.audibleyoutube.ui.element

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.huenique.audibleyoutube.model.HomeViewModel
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.ui.component.NavBar
import com.huenique.audibleyoutube.ui.element.home.HomeTopAppBar
import com.huenique.audibleyoutube.utils.RepositoryGetter

object NavRoute {
  const val HOME = "home"
  const val SEARCH = "search"
  const val LIBRARY = "library"
}

@Composable
fun MainScreen(homeViewModel: HomeViewModel) {
  val navController = rememberNavController()
  val searchResultRepository = RepositoryGetter().searchResultRepository()
  val searchWidgetState by homeViewModel.searchWidgetState

  Scaffold(
      topBar = {
        HomeTopAppBar(
            viewModel = homeViewModel,
            searchResultRepository = searchResultRepository,
            searchWidgetState = searchWidgetState)
      },
      content = {
        MainNavHost(
            navController = navController,
            homeViewModel = homeViewModel,
            searchResultRepository = searchResultRepository,
            searchWidgetState = searchWidgetState)
      },
      bottomBar = {
        NavBar(
            onHomeClick = { navController.navigate(NavRoute.HOME) },
            onSearchClick = { navController.navigate(NavRoute.SEARCH) },
            onLibraryClick = { navController.navigate(NavRoute.LIBRARY) })
      })
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    searchResultRepository: SearchResultRepository,
    searchWidgetState: SearchWidgetState
) {
  NavHost(navController = navController, startDestination = NavRoute.HOME) {
    composable(NavRoute.HOME) {
      HomeScreen(
          viewModel = homeViewModel,
          searchResultRepository = searchResultRepository,
          searchWidgetState = searchWidgetState)
    }
    composable(NavRoute.SEARCH) { SearchScreen() }
    composable(NavRoute.LIBRARY) { LibraryScreen() }
  }
}
