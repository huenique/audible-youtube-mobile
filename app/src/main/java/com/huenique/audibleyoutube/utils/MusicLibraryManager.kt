package com.huenique.audibleyoutube.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import java.util.concurrent.TimeUnit

const val MUSIC_LIBRARY_NAME = "music_library.m3u"

// File formats
const val M3U = "m3u"
const val MP3 = "mp3"
const val ENCODING = "UTF-8"

// Extended M3U Directives
const val EXTM3U = "#EXTM3U"
const val EXTENC = "#EXTENC"
const val EXTIMG = "#EXTIMG"
const val EXTINF = "#EXTINF"
const val PLAYLIST = "#PLAYLIST"

class MusicLibraryManager {
  fun createMusicLibrary(context: Context): File {
    val musicLibrary =
        File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), MUSIC_LIBRARY_NAME)
    val libraryCreated = musicLibrary.createNewFile()

    if (libraryCreated) {
      musicLibrary.appendText("$EXTM3U\n$EXTENC: $ENCODING")
    }

    return musicLibrary
  }

  fun addMusicToLibrary(m3uFile: File, audioFile: File, imageFile: File) {
    BufferedReader(FileReader(m3uFile)).use { br ->
      var line: String?

      while (br.readLine().also { line = it } != null) {
        if (audioFile.absolutePath == line) return
      }
    }

    val metaRetriever = MediaMetadataRetriever()
    metaRetriever.setDataSource(audioFile.absolutePath)

    val musicLength =
        metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
    val duration = musicLength?.let { TimeUnit.MILLISECONDS.toSeconds(it) }

    m3uFile.appendText("\n$EXTIMG:${imageFile.absolutePath}")
    m3uFile.appendText(
        "\n$EXTINF:$duration,${audioFile.nameWithoutExtension}\n${audioFile.absolutePath}")
  }

  fun removeMusicFromPlaylist() {}

  fun addPlaylist(externalFilesDir: File, playlistName: String): Boolean {
    val playlist = File(externalFilesDir, "$playlistName.$M3U")
    val isPlaylistCreated = playlist.createNewFile()

    if (isPlaylistCreated) {
      playlist.appendText("$EXTM3U\n$EXTENC: $ENCODING\n$PLAYLIST:$playlistName")
    }

    return isPlaylistCreated
  }

  fun removePlayList() {}

  fun getAllSongs(context: Context): TreeMap<String, String> {
    val songs = TreeMap<String, String>()

    context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath?.let { it ->
      File(it).walk().forEach {
        if (it.extension == MP3) {
          songs[it.nameWithoutExtension] = it.absolutePath
        }
      }
    }

    return songs
  }

  fun getSongsFromPlaylist(playlist: File): MutableMap<Int, Map<String, String>> {
    val listedSongs = mutableMapOf<Int, Map<String, String>>()

    // Collect songs listed in audio playlist file.
    BufferedReader(FileReader(playlist)).use { br ->
      var line: String?
      var entryId = 0
      var songDuration = ""
      var songTitle = ""
      var songPath = ""

      while (br.readLine().also { line = it } != null) {
        if (line!!.isEmpty()) continue

        // Check if line is a valid m3u entry.
        if (line!!.take(7) == EXTINF) {
          // Extract data after #EXTINF: (e.g. 111,Song Title)
          val songMetadata = line!!.split(":")[1].split(",")
          songDuration = songMetadata[0]
          songTitle = songMetadata[1]
        } else if (line!!.startsWith("/")) {
          songPath = line as String
        }

        if (songDuration.isNotEmpty() && songTitle.isNotEmpty() && songPath.isNotEmpty()) {
          listedSongs[entryId] =
              mapOf(
                  "songDuration" to songDuration, "songTitle" to songTitle, "songPath" to songPath)
          songDuration = ""
          songTitle = ""
          songPath = ""
        }
        entryId += 1
      }
    }

    return listedSongs
  }

  fun getSongCover(playlist: File, songTitle: String): String {
    var line: String?
    var currImg = ""

    BufferedReader(FileReader(playlist)).use { br ->
      while (br.readLine().also { line = it } != null) {
        line?.let {
          if (it.take(7) == EXTIMG) {
            currImg = it.replace("$EXTIMG:", "")
          }
        }

        if (line?.contains(songTitle) == true) {
          return currImg
        }
      }
    }
    return currImg
  }
}
