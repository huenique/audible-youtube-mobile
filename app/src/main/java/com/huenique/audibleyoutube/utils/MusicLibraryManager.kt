package com.huenique.audibleyoutube.utils

import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import androidx.core.net.toUri
import java.io.File
import java.util.concurrent.TimeUnit

const val MUSIC_LIBRARY_NAME = "music_library.m3u"

class MusicLibraryManager {
  fun createMusicLibrary(context: Context): File {
    val musicLibrary =
        File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), MUSIC_LIBRARY_NAME)
    val libraryCreated = musicLibrary.createNewFile()

    if (libraryCreated) {
      musicLibrary.appendText("#EXTM3U\n#EXTENC: UTF-8")
    }

    return musicLibrary
  }

  fun addMusicToLibrary(context: Context, musicLibrary: File, audioFilePath: File) {
    val mediaPlayer = MediaPlayer.create(context, audioFilePath.toUri())
    val musicLength = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer!!.duration.toLong())

    musicLibrary.appendText(
        "\n#EXTINF:$musicLength,${audioFilePath.name}\n${audioFilePath.absolutePath}")
  }

  fun addMusicToPlaylist(context: Context, playlistFilePath: File, audioFilePath: File) {
    val mediaPlayer = MediaPlayer.create(context, audioFilePath.toUri())
    val musicLength = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer!!.duration.toLong())

    playlistFilePath.appendText(
        "\n#EXTINF:$musicLength,${audioFilePath.name}\n${audioFilePath.absolutePath}")
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
