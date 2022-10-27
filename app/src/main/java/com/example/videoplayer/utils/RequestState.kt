package com.example.videoplayer.utils

sealed class RequestState<out V> {
    object Idle : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T?) : RequestState<T>()
    data class Error(val error: String) : RequestState<Nothing>()
}
