package com.example.videoplayer.network

import com.example.videoplayer.data.VideoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService{

    @Headers("Authorization: 563492ad6f91700001000001671f9a9177b24b868d194cdfc9566ff0")
    @GET("popular?per_page=5")
    suspend fun getVideos(): Response<VideoResponse>
}