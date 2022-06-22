package com.huenique.audibleyoutube.utils

import com.huenique.audibleyoutube.repository.SearchResultRepository

class RepositoryGetter {
    fun searchResultRepository(): SearchResultRepository {
        return SearchResultRepository()
    }
}