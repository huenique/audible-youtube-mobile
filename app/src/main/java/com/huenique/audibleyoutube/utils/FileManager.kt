package com.huenique.audibleyoutube.utils

import java.io.File

class FileManager {

  fun createFile(fileName: String) {

    val file = File(fileName)

    // create a new file
    val isNewFileCreated: Boolean = file.createNewFile()

    if (isNewFileCreated) {
      println("$fileName is created successfully.")
    } else {
      println("$fileName already exists.")
    }

    // try creating a file that already exists
    val isFileCreated: Boolean = file.createNewFile()

    if (isFileCreated) {
      println("$fileName is created successfully.")
    } else {
      println("$fileName already exists.")
    }
  }
}
