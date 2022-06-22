package com.huenique.audibleyoutube.repository


interface Repository {
    fun findAll(): Any
    fun update(value: Any)
}


class SearchResultRepository : Repository {
    private var result = "Default"

    override fun findAll(): String {
        return result
    }

    override fun update(value: Any) {
        result = value.toString()
    }
}