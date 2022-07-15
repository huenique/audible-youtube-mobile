package com.huenique.audibleyoutube.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MusicLibraryManager {
  fun addMusicToPlaylist(context: Context, playlistFilePath: File, audioFilePath: File) {
    // #EXTINF:111,Sample artist name - Sample track title
    // C:\Music\SampleMusic.mp3

    thread {
      while (!audioFilePath.exists()) {
        TimeUnit.MICROSECONDS.sleep(100L)
      }
      println(
          "Playlist: ${playlistFilePath.absolutePath}\nFile Path: $audioFilePath\nFile Uri: ${audioFilePath.toUri()}")
      val mediaPlayer = MediaPlayer.create(context, audioFilePath.toUri())
      val musicLength = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer!!.duration.toLong())

      println("Current Playlist: ${playlistFilePath.readText()}")
      playlistFilePath.appendText(
          "\n#EXTINF:$musicLength,${audioFilePath.name}\n${audioFilePath.absolutePath}")
      println("New Playlist: ${playlistFilePath.readText()}")
    }
  }

  fun removeMusicFromPlaylist() {}

  fun addPlaylist(externalFilesDir: File, playlistName: String): Boolean {
    val playlist = File(externalFilesDir, "$playlistName.m3u")
    val isPlaylistCreated = playlist.createNewFile()
    if (isPlaylistCreated) {
      playlist.appendText("#EXTM3U\n#EXTENC: UTF-8\n#PLAYLIST:$playlistName")
    }
    return isPlaylistCreated
  }

  fun removePlayList() {}
}
