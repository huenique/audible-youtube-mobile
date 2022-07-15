package com.huenique.audibleyoutube.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.MutableState
import androidx.core.net.toUri
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MusicLibraryManager {
  fun addMusicToPlaylist(context: Context, playlistFilePath: File, audioFilePath: File) {
    thread {
      while (!audioFilePath.exists()) {
        TimeUnit.MICROSECONDS.sleep(100L)
      }

      val mediaPlayer = MediaPlayer.create(context, audioFilePath.toUri())
      val musicLength = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer!!.duration.toLong())

      playlistFilePath.appendText(
          "\n#EXTINF:$musicLength,${audioFilePath.name}\n${audioFilePath.absolutePath}")
    }
  }

  fun removeMusicFromPlaylist() {}

  fun addPlaylist(
      externalFilesDir: File,
      playlistName: String,
      resultDialogue: MutableState<String>
  ): Boolean {
    val playlist = File(externalFilesDir, "$playlistName.m3u")
    val isPlaylistCreated = playlist.createNewFile()
    if (isPlaylistCreated) {
      playlist.appendText("#EXTM3U\n#EXTENC: UTF-8\n#PLAYLIST:$playlistName")
    }
    return isPlaylistCreated
  }

  fun removePlayList() {}
}
