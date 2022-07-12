package com.huenique.audibleyoutube.ui.element

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.utils.RepositoryGetter

object NavRoute {
  const val HOME = "home"
  const val LIBRARY = "library"
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
  val repositoryGetter = RepositoryGetter()
  val navController = rememberNavController()

  MainNavHost(
      navHostController = navController, viewModel = viewModel, repositoryGetter = repositoryGetter)
}

@Composable
fun MainNavHost(
    navHostController: NavHostController,
    viewModel: MainViewModel,
    repositoryGetter: RepositoryGetter
) {
  NavHost(navController = navHostController, startDestination = NavRoute.HOME) {
    composable(NavRoute.HOME) {
      HomeScreen(
          onNavigation = { navHostController.navigate(NavRoute.HOME) },
          viewModel = viewModel,
          repositoryGetter = repositoryGetter)
    }
    composable(NavRoute.LIBRARY) {
      LibraryScreen(onNavigation = { navHostController.navigate(NavRoute.HOME) })
    }
  }
}
