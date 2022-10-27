package com.example.videoplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.example.videoplayer.VideoDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {

    @Insert(onConflict = IGNORE)
    fun addVideosList(videoDataEntity: VideoDataEntity)

    @Query("SELECT * FROM video_data")
    fun getVideosList(): Flow<List<VideoDataEntity>>

    @Query("UPDATE video_data SET recentView=:recentView WHERE videoId = :videoId")
    fun updateRecent(recentView: Int, videoId: Int)

    @Query("UPDATE video_data SET noOfTimesView=:noOfTimesView WHERE videoId = :videoId")
    fun updateNoOfTimesView(noOfTimesView: Int, videoId: Int)

    @Query("SELECT * FROM video_data WHERE videoId =:videoId")
    fun getSingleVideo(videoId: Int): Flow<VideoDataEntity>
}