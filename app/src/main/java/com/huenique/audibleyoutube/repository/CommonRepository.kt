package com.huenique.audibleyoutube.repository

interface Repository<T> {
  fun getAll(): T
  fun update(value: T)
}
