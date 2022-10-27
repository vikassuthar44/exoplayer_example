package com.example.videoplayer

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.videoplayer.ui.theme.VideoPlayerTheme
import com.example.videoplayer.utils.FilterEnum
import com.example.videoplayer.utils.FilterEnum.MOST_VIEW
import com.example.videoplayer.utils.FilterEnum.RECENT_VIEW
import com.example.videoplayer.utils.RequestStateRender
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var exoPlayer: ExoPlayer?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerTheme {
                MainContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    val context = LocalContext.current
    val activity = context as MainActivity
    val homeViewModel = hiltViewModel<VideoViewModel>()
    val isFilterPopShow = remember {
        mutableStateOf(false)
    }


    val value = remember {
        mutableStateOf(0)
    }

    val isMute = remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            if(!homeViewModel.isFullScreen) {
                Row(
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding(all = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Videos Data",
                            color = Color.Black
                        )
                        Image(
                            modifier = Modifier.clickable {
                                isFilterPopShow.value = true
                            },
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "filter"
                        )
                    }

                }
            }
        }
    ) {
        RequestStateRender(
            state = homeViewModel.res.collectAsState(),
            onSuccess = {
                val videoUri = remember {
                    mutableStateOf(it[0].videoLink)
                }
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyRow(
                        modifier = Modifier.wrapContentSize(),
                        contentPadding = PaddingValues(all = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(space = 10.dp)
                    ) {
                        itemsIndexed(items = it) { index, item ->
                            if(!homeViewModel.isFullScreen) {
                                SingleVideoItem(position = index, videoData = item) { videoUrl ->
                                    videoUri.value = videoUrl
                                    value.value = value.value + 1
                                    Log.d("Error", "MainContent: ${value.value}")
                                    homeViewModel.updateMostView(videoDataEntity = item, videoList = it)
                                    homeViewModel.updateVideoRecent(videoDataEntity = item, videoList = it)
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    modifier = Modifier
                                        .padding(all = 10.dp)
                                        .clickable {
                                            (context as MainActivity).exoPlayer?.let {  exoplayer ->
                                                homeViewModel.setTwoStateVolume(isMute.value,
                                                    exoplayer
                                                )
                                            }
                                            isMute.value = !isMute.value
                                        },
                                    painter = if(isMute.value) painterResource(id = R.drawable.ic_mute) else painterResource(id = R.drawable.ic_unmute),
                                    contentDescription = "mute")
                                Image(
                                    modifier = Modifier
                                        .padding(all = 10.dp)
                                        .clickable {
                                            homeViewModel.isFullScreen = !homeViewModel.isFullScreen
                                            changeScreenRotation(context)
                                        },
                                    painter = if(homeViewModel.isFullScreen) painterResource(id = R.drawable.ic_full_screen_exit) else painterResource(id = R.drawable.ic_full_screen),
                                    contentDescription = "fullscreen")
                            }
                        }
                        VideoView(videoUri = videoUri.value, context)
                    }
                }

            },
            onError = {

            },
            onLoading = {

            }
        )
    }

    if (isFilterPopShow.value) {
        FilterPop(
            onFilterClick = {
                when(it) {
                    MOST_VIEW -> {
                        homeViewModel.filterMostViewed()
                    }

                    RECENT_VIEW -> {
                        homeViewModel.filterRecentlyView()
                    }
                }
                isFilterPopShow.value = false
            },
            onDismiss = {
                isFilterPopShow.value = false
            }
        )
    }
}

@Composable
fun SingleVideoItem(
    position: Int,
    videoData: VideoDataEntity,
    onClick: (String) -> Unit
) {
    val thumbnail = rememberAsyncImagePainter(model = videoData.videoThumbnail)
    Box(modifier = Modifier.wrapContentSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        onClick.invoke(videoData.videoLink)
                    },
                painter = thumbnail,
                contentScale = ContentScale.Crop,
                contentDescription = "thumbnail"
            )
        }
    }
}

@Composable
fun VideoView(videoUri: String, activity: MainActivity) {
    val context = LocalContext.current
     activity.exoPlayer = ExoPlayer.Builder(LocalContext.current)
        .build()
        .also { exoPlayer ->
            val mediaItem = MediaItem.Builder()
                .setUri(videoUri)
                .build()
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }

    DisposableEffect(
        AndroidView(factory = {
            StyledPlayerView(context).apply {
                player = activity.exoPlayer
            }
        })
    ) {
        onDispose { activity.exoPlayer!!.release() }
    }
}

@Composable
fun FilterPop(
    onFilterClick: (FilterEnum) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp)
            .clickable(
                onClick = {
                    onDismiss.invoke()
                },
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null
            ),
        contentAlignment = Companion.TopEnd
    ) {
        Box(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(size = 20.dp))
                .border(color = Color.Gray, width = 1.dp, shape = RoundedCornerShape(size = 10.dp))
                .padding(all = 10.dp)
                .clickable {
                    onDismiss.invoke()
                },
            contentAlignment = Companion.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 10.dp),
            ) {
                Text(modifier = Modifier.clickable {
                    onFilterClick.invoke(FilterEnum.RECENT_VIEW)
                }, text = "Recently viewed")
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .background(color = Color.Gray)
                )
                Text(modifier = Modifier.clickable {
                    onFilterClick.invoke(FilterEnum.MOST_VIEW)
                }, text = "Most viewed")
            }
        }
    }
}

fun changeScreenRotation(activity: MainActivity) {
    val orientation: Int = activity.resources.configuration.orientation
     if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    } else {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    }
}