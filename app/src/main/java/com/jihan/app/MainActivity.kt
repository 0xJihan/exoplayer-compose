package com.jihan.app

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import com.jihan.app.ui.theme.AppTheme


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()





        setContent {
            AppTheme {


            }
        }
    }

    @OptIn(UnstableApi::class)
    @Composable
    fun MyPlayer() {

        var isBuffering by remember { mutableStateOf(true) }
        var fullScreen by remember { mutableStateOf(false) }

        //? for local video or video url
//        val url = "https://www.sample-videos.com/video321/mp4/720/big_buck_bunny_720p_30mb.mp4"
//        val localUrl = "android.resource://${application.packageName}/${R.raw.sample}"


        val liveStreamingUrl =
            "https://live.relentlessinnovations.net:1936/imantv/imantv/playlist.m3u8"
        val mediaItem = MediaItem.fromUri(liveStreamingUrl)

        //? media source for online video url
//        val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
//            .createMediaSource(mediaItem)

        //? media source for live streaming url
        val hlsMediaSource =
            HlsMediaSource.Factory(DefaultHttpDataSource.Factory()).createMediaSource(mediaItem)

        val exoPlayer = remember {
            ExoPlayer.Builder(applicationContext).build().apply {
                setMediaSource(hlsMediaSource)
                //* use  setMediaItem(mediaItem) for local media play
                prepare()
                playWhenReady = true
            }
        }



        Box {


            AndroidView(
                {
                    PlayerView(it).also { playerView ->
                        playerView.player = exoPlayer
                    }
                },
                if (fullScreen) Modifier.fillMaxSize() else Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )

            AnimatedVisibility(
                isBuffering,
                Modifier.align(Alignment.Center)
            ) { CircularProgressIndicator() }

            IconButton(onClick = {
                fullScreen = !fullScreen
                toggleFullscreen(this@MainActivity, fullScreen)
            },
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(15.dp)) {
                Icon(
                    Icons.Default.Refresh,
                    null,
                    tint = Color.White
                )
            }
        }





        DisposableEffect(Unit) {

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    isBuffering = playbackState == Player.STATE_BUFFERING
                }
            })

            onDispose {
                exoPlayer.release()
            }
        }

    }


}

fun toggleFullscreen(activity: Activity, isFullscreen: Boolean) {
    val windowInsetsController =
        WindowInsetsControllerCompat(activity.window, activity.window.decorView)
    if (isFullscreen) {
        // Enter fullscreen and rotate to landscape
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    } else {
        // Exit fullscreen and rotate to portrait
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}

