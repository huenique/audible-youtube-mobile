package com.huenique.audibleyoutube.screen

import android.media.MediaPlayer
import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.huenique.audibleyoutube.R
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
import java.io.File
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
  val currentPlaylistContent by mainViewModel.currentPlaylistContent
  val currentPlaylist by mainViewModel.currentPlaylist
  val currentSongCover by mainViewModel.currentSongCover

  // Setup app-wide notification channel so we don't have to instantiate it everytime
  LaunchedEffect(Unit) {
    notificationManager.createNotificationChannel(channelId = "AudibleYouTubeChannel", context)
  }

  // Auto play next song in the playlist
  mediaPlayer.setOnCompletionListener {
    val nextSongPath = currentPlaylistContent.higherEntry(currentSongPlaying)?.value
    nextSongPath?.let { songTitle: String ->
      mainViewModel.updateCurrentSongPlaying(newValue = File(songTitle).nameWithoutExtension)
      mainViewModel.viewModelScope.launch {
        withContext(Dispatchers.IO) {
          try {
            it.reset()
            it.setDataSource(context, File(nextSongPath).toUri())
            it.prepare()
            it.start()
          } catch (e: IllegalStateException) {
            e.printStackTrace()
          }
        }
      }

      // Point to the path of the song's thumbnail/cover
      //      if (currentSongPlaying.isNotEmpty()) {
      //        val playlist =
      //            File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), currentPlaylist)
      //        val songCover = musicLibraryManager.getSongCover(playlist, currentSongPlaying)
      //        mainViewModel.updateCurrentSongCover(newValue = songCover)
      //
      //        println(
      //            "\nPLAYLIST: $playlist\nCURRENT SONG: $currentSongPlaying \nSONG COVER:
      // $currentSongCover")
      //      }
    }
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

  Scaffold(
      topBar = {
        when (navBackStackEntry?.destination?.route) {
          NavigationRoute.PLAYLIST -> {
            TopAppBar(
                title = { Text(text = "All songs") },
                navigationIcon = {
                  IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_back),
                        contentDescription = "All songs return button")
                  }
                })
          }
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
            currentSongCover = currentSongCover,
            onNavigate = { mainViewModel.updateScreenNavState(newValue = it) },
            audibleYoutube = audibleYoutube,
            musicLibraryManager = musicLibraryManager,
            httpResponseHandler = httpResponseHandler,
            mediaPlayer = mediaPlayer,
            currentPlaylist = currentPlaylist,
            currentPlaylistContent = currentPlaylistContent)
      },
      bottomBar = {
        Column {
          if (showMiniPlayer) {
            MinimizedPlayer(
                playButtonState = playButtonState,
                currentSongPlaying = currentSongPlaying,
                currentSongCover = currentSongCover,
                onPlayerClick = { navController.navigate(NavigationRoute.PLAYER) },
                onPlayClick = {
                  when (playButtonState) {
                    PlayButtonState.PAUSED -> {
                      mediaPlayer.start()
                      mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PLAYING)
                    }
                    PlayButtonState.PLAYING -> {
                      mediaPlayer.pause()
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
    currentSongCover: String,
    onNavigate: (ScreenNavigationState) -> Unit,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    httpResponseHandler: HttpResponseHandler,
    mediaPlayer: MediaPlayer,
    currentPlaylist: String,
    currentPlaylistContent: TreeMap<String, String>
) {
  val context = LocalContext.current

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
          playButtonState = playButtonState,
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
      mainViewModel.updateCurrentPlaylist(newValue = "music_library.m3u")
      mainViewModel.updateCurrentPlaylistContent(newValue = songs)
      AllSongs(
          playButtonState = playButtonState,
          currentSongPlaying = currentSongPlaying,
          songs = songs,
          onSongClick = { songTitle: String, songPath: String ->
            if (playButtonState == PlayButtonState.PAUSED) {
              mediaPlayer.start()
              mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PLAYING)
            } else if (songTitle == currentSongPlaying &&
                playButtonState == PlayButtonState.PLAYING) {
              mediaPlayer.pause()
              mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PAUSED)
            }

            if (currentSongPlaying != songTitle) {
              if (currentSongPlaying.isNotEmpty()) {
                mainViewModel.updateCurrentSongPlaying(newValue = "")
              }
              mainViewModel.updateCurrentSongPlaying(newValue = songTitle)
              mainViewModel.viewModelScope.launch {
                withContext(Dispatchers.IO) {
                  mediaPlayer.reset()
                  mediaPlayer.setDataSource(context, File(songPath).toUri())
                  mediaPlayer.prepare()
                  mediaPlayer.start()
                }
              }
            }

            val playlist =
                File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), currentPlaylist)
            val songCover = musicLibraryManager.getSongCover(playlist, songTitle)
            mainViewModel.updateCurrentSongCover(newValue = songCover)
          })
    }
    composable(NavigationRoute.PLAYER) {
      onNavigate(ScreenNavigationState.PLAYER)
      MaximizedPlayer(
          playButtonState = playButtonState,
          currentSongPlaying = currentSongPlaying,
          currentSongCover = currentSongCover,
          onPlayClick = {
            when (playButtonState) {
              PlayButtonState.PAUSED -> {
                mediaPlayer.start()
                mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PLAYING)
              }
              PlayButtonState.PLAYING -> {
                mediaPlayer.pause()
                mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PAUSED)
              }
            }
          },
          onForwardClick = {
            val nextSongPath = currentPlaylistContent.higherEntry(currentSongPlaying)?.value
            nextSongPath?.let {
              mainViewModel.updateCurrentSongPlaying(newValue = File(it).nameWithoutExtension)
              val playlist =
                  File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), currentPlaylist)
              val songCover = musicLibraryManager.getSongCover(playlist, it)
              mainViewModel.updateCurrentSongCover(newValue = songCover)
            }

            mainViewModel.viewModelScope.launch {
              try {
                withContext(Dispatchers.IO) {
                  mediaPlayer.reset()
                  mediaPlayer.setDataSource(context, File(nextSongPath).toUri())
                  mediaPlayer.prepare()
                  mediaPlayer.start()
                }
              } catch (e: Exception) {
                e.printStackTrace()
              }
            }
          },
          onBackClick = {
            val nextSongPath = currentPlaylistContent.lowerEntry(currentSongPlaying)?.value
            nextSongPath?.let {
              mainViewModel.updateCurrentSongPlaying(newValue = File(it).nameWithoutExtension)
              val playlist =
                  File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), currentPlaylist)
              val songCover = musicLibraryManager.getSongCover(playlist, it)
              mainViewModel.updateCurrentSongCover(newValue = songCover)
            }

            mainViewModel.viewModelScope.launch {
              withContext(Dispatchers.IO) {
                try {
                  mediaPlayer.reset()
                  mediaPlayer.setDataSource(context, File(nextSongPath).toUri())
                  mediaPlayer.prepare()
                  mediaPlayer.start()
                } catch (e: Exception) {
                  e.printStackTrace()
                }
              }
            }
          },
          onArrowDownClick = { navController.popBackStack() })
    }
  }
}
