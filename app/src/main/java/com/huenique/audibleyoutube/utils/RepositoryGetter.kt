package com.huenique.audibleyoutube.utils

import com.huenique.audibleyoutube.repository.HttpResponseRepository

class RepositoryGetter {
  fun httpResponseRepository(): HttpResponseRepository {
    return HttpResponseRepository()
  }
}
