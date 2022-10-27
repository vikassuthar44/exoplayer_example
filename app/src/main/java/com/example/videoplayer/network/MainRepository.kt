package com.example.videoplayer.network

import com.example.videoplayer.network.ApiHelper
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper: ApiHelper
){
    suspend fun getVideos() = apiHelper.getVideos()
}