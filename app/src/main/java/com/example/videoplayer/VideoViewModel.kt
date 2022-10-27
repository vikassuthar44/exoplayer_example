package com.example.videoplayer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.network.MainRepository
import com.example.videoplayer.room.DatabaseRepository
import com.example.videoplayer.utils.RequestState
import com.example.videoplayer.utils.RequestState.Idle
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _res = MutableStateFlow<RequestState<List<VideoDataEntity>>>(Idle)

    val res = _res.asStateFlow()

    var isFullScreen = false
    init {
        getVideos()
    }

    private fun getVideos() = viewModelScope.launch(Dispatchers.IO) {
        _res.value = RequestState.Loading
        databaseRepository.getVideosList().collect() {
            if (it.isEmpty()) {
                mainRepository.getVideos().let { response ->
                    if (response.isSuccessful) {
                        Log.d("VideoViewModel", "getVideos: from server")
                        for ((index, video) in response.body()?.videos!!.withIndex()) {
                            val videoDataEntity = VideoDataEntity(
                                videoId = video.videoId.toInt(),
                                videoLink = video.videoFiles[0].link,
                                videoThumbnail = video.image,
                                noOfTimesView = 0,
                                recentView = index
                            )
                            databaseRepository.addVideosList(videoDataEntity)
                        }
                        databaseRepository.getVideosList().collect() { videoList ->
                            _res.value = RequestState.Success(videoList)
                        }
                    } else {
                        Log.d("VideoViewModel", "getVideos: error from server")
                        _res.value = RequestState.Error(response.errorBody().toString())
                    }
                }
            } else {
                Log.d("VideoViewModel", "getVideos: from database")
                databaseRepository.getVideosList().collect() { videoList ->
                    _res.value = RequestState.Success(videoList)
                }
            }
        }

    }

    fun filterMostViewed() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getVideosList().collect() {
                it.sortedBy { videoEntity ->
                    videoEntity.noOfTimesView
                }
                _res.value = RequestState.Success(it)
            }
        }
    }

    fun filterRecentlyView() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getVideosList().collect() {
                it.sortedBy { video ->
                    video.recentView
                }
                _res.value = RequestState.Success(it)
            }
        }
    }

    fun updateMostView(videoDataEntity: VideoDataEntity, videoList: List<VideoDataEntity>) {
        Log.d("Error", "updateMostView: ")
        viewModelScope.launch(Dispatchers.IO) {
            for (video in videoList) {
                if (videoDataEntity.videoId == video.videoId) {
                    val previousViewValue = video.noOfTimesView + 1
                    databaseRepository.updateMostView(previousViewValue, videoId = videoDataEntity.videoId)
                    Log.d("Error", "updateMostView: called")
                }
            }
        }
    }

    fun updateVideoRecent(videoDataEntity: VideoDataEntity, videoList: List<VideoDataEntity>) {
        Log.d("Error", "updateVideoRecent: ")
        viewModelScope.launch(Dispatchers.IO) {
            for (video in videoList) {
                if (videoDataEntity.videoId == video.videoId) {
                    databaseRepository.updateVideoRecent(recentView = 0, videoId = video.videoId)
                    Log.d("Error", "updateVideoRecent: called same")
                } else if (videoDataEntity.recentView > video.recentView) {
                    val previousRecentView = video.recentView + 1
                    databaseRepository.updateVideoRecent(recentView = previousRecentView, videoId = video.videoId)
                    Log.d("Error", "updateVideoRecent: called")
                }
            }
        }
    }

    fun setTwoStateVolume(mute : Boolean, exoPlayer: ExoPlayer) {
        exoPlayer.volume = if (mute) 0F else 1F
    }
}