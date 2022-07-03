package com.huenique.audibleyoutube.utils

import com.huenique.audibleyoutube.repository.SearchResultRepository

class RepositoryClassMethod {
  fun searchResultRepository(): SearchResultRepository {
    return SearchResultRepository()
  }
}
