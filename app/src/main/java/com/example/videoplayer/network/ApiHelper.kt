package com.example.videoplayer.network

import com.example.videoplayer.data.VideoResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun getVideos(): Response<VideoResponse>
}