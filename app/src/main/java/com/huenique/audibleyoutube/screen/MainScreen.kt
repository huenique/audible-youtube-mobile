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
import com.huenique.audibleyoutube.state.*
import com.huenique.audibleyoutube.utils.*
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
    recentManager: RecentManager,
    notificationManager: NotificationManager,
    httpResponseHandler: HttpResponseHandler,
    mediaPlayer: MediaPlayer
) {
  val context = LocalContext.current
  recentManager.createRecentDb(context)

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
  val currentSongDuration by mainViewModel.currentSongDuration

  // Setup app-wide notification channel so we don't have to instantiate it everytime.
  LaunchedEffect(Unit) {
    notificationManager.createNotificationChannel(channelId = "AudibleYouTubeChannel", context)
  }

  mediaPlayer.setOnPreparedListener {
    mainViewModel.updateCurrentSongDuration(newValue = it.duration.toFloat())
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
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }

      val playlist = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), currentPlaylist)
      val songCover = musicLibraryManager.getSongCover(playlist, songTitle)

      mainViewModel.updateCurrentSongCover(newValue = songCover)
      recentManager.addToRecentlyPlayed(context, songCover)
    }
  }

  // Avoid "Screen Reset" error screen
  when (navBackStackEntry?.destination?.route) {
    NavigationRoute.PLAYLIST,
    NavigationRoute.PLAYER,
    NavigationRoute.HOME,
    NavigationRoute.LIBRARY -> {
      mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
      mainViewModel.updatePlaylistState(newValue = PlaylistState.CLOSED)
      mainViewModel.updateSpinnerState(newValue = false)

      // Reset repos to prevent memory hogging
      mainViewModel.updateSearchRepoState(newValue = HttpResponseRepositoryState.INTERRUPTED)
      httpResponseRepo.update(value = "{}")
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
                title = {
                  Text(
                      text =
                          if (currentPlaylist != "music_library.m3u")
                              currentPlaylist.replace(".m3u", "")
                          else "All songs")
                },
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
            currentSongDuration = currentSongDuration,
            audibleYoutube = audibleYoutube,
            musicLibraryManager = musicLibraryManager,
            recentManager = recentManager,
            httpResponseHandler = httpResponseHandler,
            mediaPlayer = mediaPlayer,
            currentPlaylist = currentPlaylist,
            currentPlaylistContent = currentPlaylistContent,
            onNavigate = { mainViewModel.updateScreenNavState(newValue = it) })
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
                onForwardClick = {
                  val nextSongPath = currentPlaylistContent.higherEntry(currentSongPlaying)?.value
                  nextSongPath?.let {
                    mainViewModel.updateCurrentSongPlaying(newValue = File(it).nameWithoutExtension)
                    val playlist =
                        File(
                            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                            currentPlaylist)
                    val songCover = musicLibraryManager.getSongCover(playlist, it)

                    mainViewModel.updateCurrentSongCover(newValue = songCover)
                    recentManager.addToRecentlyPlayed(context, songCover)
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

                  mainViewModel.updateCurrentSongDuration(mediaPlayer.duration.toFloat())
                })
          }
          if (showBottomBar && showMiniPlayer) {
            NavBar(
                onHomeClick = { navController.navigate(NavigationRoute.HOME) },
                onSearchClick = {
                  mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                },
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
    currentSongDuration: Float,
    audibleYoutube: AudibleYoutubeApi,
    musicLibraryManager: MusicLibraryManager,
    recentManager: RecentManager,
    httpResponseHandler: HttpResponseHandler,
    mediaPlayer: MediaPlayer,
    currentPlaylist: String,
    currentPlaylistContent: TreeMap<String, String>,
    onNavigate: (ScreenNavigationState) -> Unit
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
          recentManager = recentManager,
          httpResponseHandler = httpResponseHandler)
    }
    composable(NavigationRoute.LIBRARY) {
      onNavigate(ScreenNavigationState.LIBRARY)
      LibraryScreen(
          viewModel = mainViewModel,
          navController = navController,
          searchWidgetState = searchWidgetState,
          httpResponseRepository = httpResponseRepository,
          audibleYoutube = audibleYoutube,
          musicLibraryManager = musicLibraryManager,
          recentManager = recentManager,
          httpResponseHandler = httpResponseHandler,
          onClickAllSongs = {
            val songs = musicLibraryManager.getAllSongs(context)
            mainViewModel.updateCurrentPlaylistContent(newValue = songs)
            mainViewModel.updateCurrentPlaylist(newValue = "music_library.m3u")
            navController.navigate(NavigationRoute.PLAYLIST)
          })
    }
    composable(NavigationRoute.PLAYLIST) {
      onNavigate(ScreenNavigationState.PLAYLIST)
      Playlist(
          playButtonState = playButtonState,
          currentSongPlaying = currentSongPlaying,
          songs = currentPlaylistContent,
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
                  try {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(context, File(songPath).toUri())
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                  } catch (e: Exception) {
                    e.printStackTrace()
                  }
                }
              }
            }

            val playlist =
                File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), currentPlaylist)
            val songCover = musicLibraryManager.getSongCover(playlist, songTitle)

            mainViewModel.updateCurrentSongCover(newValue = songCover)
            recentManager.addToRecentlyPlayed(context, songCover)
          },
          onDeleteSong = { songTitle: String ->
            musicLibraryManager.removeSongFromLibrary(context, songTitle)

            if (currentSongPlaying == songTitle) {
              mediaPlayer.reset()
              mainViewModel.updateCurrentSongPlaying(newValue = "")
              mainViewModel.updateCurrentSongCover(newValue = "")
              mainViewModel.updatePlayButtonState(newValue = PlayButtonState.PAUSED)
            }

            val playlist =
                File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), currentPlaylist)
            val songs = musicLibraryManager.getSongsFromPlaylist(playlist)
            mainViewModel.updateCurrentPlaylistContent(newValue = songs)
          })
    }
    composable(NavigationRoute.PLAYER) {
      onNavigate(ScreenNavigationState.PLAYER)
      MaximizedPlayer(
          playButtonState = playButtonState,
          currentSongPlaying = currentSongPlaying,
          currentSongCover = currentSongCover,
          currentSongDuration = currentSongDuration,
          mediaPlayer = mediaPlayer,
          onLaunch = { mainViewModel.updateCurrentSongDuration(it) },
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
              recentManager.addToRecentlyPlayed(context, songCover)
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

            mainViewModel.updateCurrentSongDuration(mediaPlayer.duration.toFloat())
          },
          onBackClick = {
            val nextSongPath = currentPlaylistContent.lowerEntry(currentSongPlaying)?.value
            nextSongPath?.let {
              val playlist =
                  File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), currentPlaylist)
              val songCover = musicLibraryManager.getSongCover(playlist, it)

              mainViewModel.updateCurrentSongPlaying(newValue = File(it).nameWithoutExtension)
              mainViewModel.updateCurrentSongCover(newValue = songCover)
              recentManager.addToRecentlyPlayed(context, songCover)
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

            mainViewModel.updateCurrentSongDuration(mediaPlayer.duration.toFloat())
          },
          onArrowDownClick = { navController.popBackStack() })
    }
  }
}
