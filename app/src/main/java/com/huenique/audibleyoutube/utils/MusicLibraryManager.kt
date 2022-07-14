package com.huenique.audibleyoutube.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

class MusicLibraryManager {
  fun addMusicToPlaylist(context: Context, playlistFilePath: File, audioFilePath: File) {
    println(
        "Playlist: ${playlistFilePath.absolutePath}\nFile Path: $audioFilePath\nFile Uri: ${audioFilePath.toUri()}")
    val mediaPlayer = MediaPlayer.create(context, audioFilePath.toUri())
    // mediaPlayer.setOnPreparedListener { println(mediaPlayer.duration) }
    // playlistFilePath.appendText(audioFilePath.absolutePath)
  }
  fun removeMusicFromPlaylist() {}
  fun addPlaylist() {}
  fun removePlayList() {}
}
