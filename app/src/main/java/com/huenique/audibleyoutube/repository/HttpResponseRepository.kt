package com.huenique.audibleyoutube.repository

class HttpResponseRepository : Repository<String> {
  private var content = "{}"
  private var error = "{}"

  override fun getContent(): String {
    return content
  }

  override fun getError(): String {
    return error
  }

  override fun updateContent(value: String) {
    content = value
  }

  override fun updateError(value: String) {
    error = value
  }
}
