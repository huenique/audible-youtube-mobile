package com.huenique.audibleyoutube.repository

class SearchResultRepository : Repository<String> {
  private var content = "{}"

  override fun getAll(): String {
    return content
  }

  override fun update(value: String) {
    content = value
  }
}
