package com.huenique.audibleyoutube.repository

interface Repository<T> {
  fun getAll(): T
  fun update(value: T)
}

class SearchResultRepository : Repository<String> {
  private var content = "{}"

  override fun getAll(): String {
    return content
  }

  override fun update(value: String) {
    content = value
  }
}
