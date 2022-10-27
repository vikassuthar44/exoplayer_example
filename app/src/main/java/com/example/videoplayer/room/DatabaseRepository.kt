package com.example.videoplayer.room


import com.example.videoplayer.VideoDataEntity
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {

    suspend fun getVideosList(): Flow<List<VideoDataEntity>>

    suspend fun addVideosList(videoDataEntity: VideoDataEntity)

    suspend fun updateVideoRecent(recentView: Int, videoId: Int)

    suspend fun updateMostView(mostView: Int, videoId: Int)

    suspend fun getSingleVideo(videoId: Int): Flow<VideoDataEntity>
}