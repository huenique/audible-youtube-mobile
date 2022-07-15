package com.huenique.audibleyoutube.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
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

  fun addMusicToLibrary(context: Context, m3uFile: File, audioFile: File) {
    val metaRetriever = MediaMetadataRetriever()
    metaRetriever.setDataSource(audioFile.absolutePath)

    val musicLength =
        metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
    val duration = musicLength?.let { TimeUnit.MILLISECONDS.toSeconds(it.toLong()) }

    m3uFile.appendText("\n#EXTINF:$duration,${audioFile.name}\n${audioFile.absolutePath}")
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
