package com.huenique.audibleyoutube.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.PrintWriter

const val RECENTLY_PLAYED = "recently_played.txt"
const val RECENTLY_ADDED = "recently_added.txt"

class RecentManager {
  private fun addToRecent(outFile: File, songCoverPath: String) {
    val songs = outFile.readLines().toMutableList()

    if (songCoverPath.isEmpty()) return

    if (songs.size == 6) {
      songs.removeLast()
    }

    songs.removeAll(listOf(songCoverPath))
    songs.add(0, songCoverPath)

    val writer = PrintWriter(outFile)
    writer.write("")
    writer.close()

    for (song in songs) {
      outFile.appendText(song)
      outFile.appendText("\n")
    }
  }

  private fun removeCover(outFile: File, songCoverPath: String) {
    val songs = outFile.readLines().toMutableList()

    if (songCoverPath.isEmpty()) return

    songs.removeAll(listOf(songCoverPath))

    val writer = PrintWriter(outFile)
    writer.write("")
    writer.close()

    for (song in songs) {
      outFile.appendText(song)
      outFile.appendText("\n")
    }
  }

  fun removeFromRecentlyAdded(context: Context, songCoverPath: String) {
    val recentAdd =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), RECENTLY_ADDED)

    removeCover(recentAdd, songCoverPath)
  }

  fun removeFromRecentlyPlayed(context: Context, songCoverPath: String) {
    val recentPlay =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), RECENTLY_PLAYED)

    removeCover(recentPlay, songCoverPath)
  }

  fun createRecentDb(context: Context) {
    val recentlyPlayedDb =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), RECENTLY_PLAYED)
    val recentlyAddedDb =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), RECENTLY_ADDED)

    recentlyPlayedDb.createNewFile()
    recentlyAddedDb.createNewFile()
  }

  fun addToRecentlyAdded(context: Context, songCoverPath: String) {
    addToRecent(
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), RECENTLY_ADDED),
        songCoverPath)
  }

  fun addToRecentlyPlayed(context: Context, songCoverPath: String) {
    addToRecent(
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), RECENTLY_PLAYED),
        songCoverPath)
  }

  fun getRecentlyAdded(context: Context): List<String> {
    val addedSongs =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), RECENTLY_ADDED)
    return addedSongs.readLines()
  }

  fun getRecentlyPlayed(context: Context): List<String> {
    val playedSongs =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), RECENTLY_PLAYED)
    return playedSongs.readLines()
  }
}
