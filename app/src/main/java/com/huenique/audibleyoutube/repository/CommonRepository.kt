package com.huenique.audibleyoutube.repository

interface Repository<T> {
  fun getContent(): T
  fun getError(): T
  fun updateContent(value: T)
  fun updateError(value: T)
}
