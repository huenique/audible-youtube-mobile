package com.huenique.audibleyoutube.screen

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.huenique.audibleyoutube.component.MaximizedPlayer
import com.huenique.audibleyoutube.component.MinimizedPlayer
import com.huenique.audibleyoutube.component.NavBar
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.screen.main.MainTopAppBar
import com.huenique.audibleyoutube.service.AudibleYoutubeApi
import com.huenique.audibleyoutube.state.PlayButtonState
import com.huenique.audibleyoutube.state.ScreenNavigationState
import com.huenique.audibleyoutube.state.SearchWidgetState
import com.huenique.audibleyoutube.utils.HttpResponseHandler
import com.huenique.audibleyoutube.utils.MusicLibraryManager
import com.huenique.audibleyoutube.utils.NotificationManager
import com.huenique.audibleyoutube.utils.RepositoryGetter

object NavigationRoute {
  const val HOME = "home"
  const val SEARCH = "search"
  const val LIBRARY = "library"
  const val PLAYLIST = "playlist"
  const val PLAYER = "player"
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    notificationManager: NotificationManager,
    httpResponseHandler: HttpResponseHandler,
    mediaPlayer: MediaPlayer
) {
  val context = LocalContext.current

  // Navigation controller
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  var showBottomBar by rememberSaveable { mutableStateOf(value = true) }
  var showMiniPlayer by rememberSaveable { mutableStateOf(value = false) }

  // View model states and dependencies
  val httpResponseRepo = RepositoryGetter().httpResponseRepository()
  val searchWidgetState by mainViewModel.searchWidgetState
  val screenNavigationState by mainViewModel.screenNavigationState
  val playButtonState by mainViewModel.playButtonState
  val currentSongPlaying by mainViewModel.currentSongPlaying

  LaunchedEffect(Unit) {
    notificationManager.createNotificationChannel(channelId = "AudibleYouTubeChannel", context)
  }

  showBottomBar =
      when (navBackStackEntry?.destination?.route) {
        NavigationRoute.PLAYLIST -> false
        else -> true
      }

  showMiniPlayer =
      when (navBackStackEntry?.destination?.route) {
        NavigationRoute.PLAYER -> false
        else -> true
      }

  // TODO: Create view/screen for playlists
  // When a song is selected/tapped in a specified playlist:
  // 1. Save/remember the song title
  // 2. Read playlist file
  // 3. Pass the file object to getSongsFromPlaylist()
  // 4. Pass the return data to AudioPlayer
  // 5. Play the song from AudioPlayer
  // 6. Remember the current song playing
  // 7. On skip, simply play the next song in the data returned by getSongsFromPlaylist()
  Scaffold(
      topBar = {
        when (navBackStackEntry?.destination?.route) {
          NavigationRoute.PLAYLIST -> TopAppBar(title = { Text(text = "All songs") })
          NavigationRoute.PLAYER -> {}
          else ->
              MainTopAppBar(
                  viewModel = mainViewModel,
                  httpResponseRepository = httpResponseRepo,
                  searchWidgetState = searchWidgetState,
                  screenNavigationState = screenNavigationState,
                  navigationRoute = NavigationRoute,
                  httpResponseHandler = httpResponseHandler)
        }
      },
      content = {
        MainNavHost(
            navController = navController,
            mainViewModel = mainViewModel,
            httpResponseRepository = httpResponseRepo,
            searchWidgetState = searchWidgetState,
            playButtonState = playButtonState,
            currentSongPlaying = currentSongPlaying,
            onNavigate = { mainViewModel.updateScreenNavState(newValue = it) },
            audibleYoutube = audibleYoutube,
            musicLibraryManager = musicLibraryManager,
            httpResponseHandler = httpResponseHandler,
            mediaPlayer = mediaPlayer,
        )
      },
      bottomBar = {
        Column {
          if (showMiniPlayer) {
            MinimizedPlayer(
                playButtonState = playButtonState,
                currentSongPlaying = currentSongPlaying,
                onPlayerClick = { navController.navigate(NavigationRoute.PLAYER) },
                onPlayClick = {
                  mediaPlayer.start()
                  when (playButtonState) {
                    PlayButtonState.PAUSED -> {
                      mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PLAYING)
                    }
                    PlayButtonState.PLAYING -> {
                      mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PAUSED)
                    }
                  }
                },
                onForwardClick = {})
          }
          if (showBottomBar && showMiniPlayer) {
            NavBar(
                onHomeClick = { navController.navigate(NavigationRoute.HOME) },
                onSearchClick = { navController.navigate(NavigationRoute.SEARCH) },
                onLibraryClick = { navController.navigate(NavigationRoute.LIBRARY) })
          }
        }
      })
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    httpResponseRepository: HttpResponseRepository,
    searchWidgetState: SearchWidgetState,
    playButtonState: PlayButtonState,
    currentSongPlaying: String,
    onNavigate: (ScreenNavigationState) -> Unit,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    httpResponseHandler: HttpResponseHandler,
    mediaPlayer: MediaPlayer
) {
  NavHost(navController = navController, startDestination = NavigationRoute.HOME) {
    composable(NavigationRoute.HOME) {
      onNavigate(ScreenNavigationState.HOME)
      HomeScreen(
          viewModel = mainViewModel,
          httpResponseRepository = httpResponseRepository,
          searchWidgetState = searchWidgetState,
          audibleYoutube = audibleYoutube,
          musicLibraryManager = musicLibraryManager,
          httpResponseHandler = httpResponseHandler)
    }
    composable(NavigationRoute.SEARCH) {
      onNavigate(ScreenNavigationState.SEARCH)
      SearchScreen(
          viewModel = mainViewModel,
          httpResponseRepository = httpResponseRepository,
          audibleYoutube = audibleYoutube,
          musicLibraryManager = musicLibraryManager,
          httpResponseHandler = httpResponseHandler)
    }
    composable(NavigationRoute.LIBRARY) {
      onNavigate(ScreenNavigationState.LIBRARY)
      LibraryScreen(
          viewModel = mainViewModel,
          httpResponseRepository = httpResponseRepository,
          searchWidgetState = searchWidgetState,
          audibleYoutube = audibleYoutube,
          mediaPlayer = mediaPlayer,
          musicLibraryManager = musicLibraryManager,
          httpResponseHandler = httpResponseHandler,
          onClickAllSongs = { navController.navigate(NavigationRoute.PLAYLIST) })
    }
    composable(NavigationRoute.PLAYLIST) {
      onNavigate(ScreenNavigationState.PLAYLIST)
      val songs = musicLibraryManager.getAllSongs(LocalContext.current)
      AllSongs(viewModel = mainViewModel, songs = songs, mediaPlayer = mediaPlayer)
    }
    composable(NavigationRoute.PLAYER) {
      onNavigate(ScreenNavigationState.PLAYER)
      MaximizedPlayer(
          playButtonState = playButtonState,
          currentSongPlaying = currentSongPlaying,
          onPlayClick = {
            when (playButtonState) {
              PlayButtonState.PAUSED -> {
                mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PLAYING)
              }
              PlayButtonState.PLAYING -> {
                mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PAUSED)
              }
            }
          },
          onForwardClick = { /*TODO*/},
          onBackClick = { /*TODO*/},
          onArrowDownClick = { navController.popBackStack() })
    }
  }
}
