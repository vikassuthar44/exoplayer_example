package com.example.videoplayer.network

import com.example.videoplayer.data.VideoResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
):ApiHelper{
    override suspend fun getVideos(): Response<VideoResponse> = apiService.getVideos()
}