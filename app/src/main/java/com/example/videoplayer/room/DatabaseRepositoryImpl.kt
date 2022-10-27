package com.example.videoplayer.room


import com.example.videoplayer.VideoDataEntity
import kotlinx.coroutines.flow.Flow

class DatabaseRepositoryImpl constructor(
    private val databaseDao: DatabaseDao
): DatabaseRepository {

    override suspend fun getVideosList(): Flow<List<VideoDataEntity>>  = databaseDao.getVideosList()

    override suspend fun addVideosList(videoDataEntity: VideoDataEntity) = databaseDao.addVideosList(videoDataEntity = videoDataEntity)

    override suspend fun updateVideoRecent(recentView: Int, videoId: Int) = databaseDao.updateRecent(recentView = recentView, videoId = videoId)

    override suspend fun updateMostView(mostView: Int, videoId: Int) = databaseDao.updateNoOfTimesView(noOfTimesView = mostView, videoId = videoId)

    override suspend fun getSingleVideo(videoId: Int): Flow<VideoDataEntity> = databaseDao.getSingleVideo(videoId = videoId)
}