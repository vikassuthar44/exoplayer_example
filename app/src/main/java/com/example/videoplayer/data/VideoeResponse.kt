package com.example.videoplayer.data

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("videos")
    val videos: List<VideoData>
) {

    data class VideoData(
        @SerializedName("id")
        val videoId: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("image")
        val image: String,
        @SerializedName("video_files")
        val videoFiles: List<VideoFiles>
    ) {
        data class VideoFiles(
            @SerializedName("link")
            val link: String
        )
    }
}