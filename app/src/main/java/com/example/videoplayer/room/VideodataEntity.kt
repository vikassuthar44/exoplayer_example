package com.example.videoplayer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_data")
data class VideoDataEntity(
    @PrimaryKey
    val videoId: Int,
    val videoLink: String,
    val videoThumbnail: String,
    val noOfTimesView: Int,
    val recentView: Int

)
