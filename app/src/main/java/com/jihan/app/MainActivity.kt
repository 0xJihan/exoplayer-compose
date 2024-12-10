package com.jihan.app

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jihan.app.model.Destination
import com.jihan.app.screens.VideoListScreen
import com.jihan.app.ui.theme.AppTheme
import com.jihan.app.viewmodel.VideoViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()





        setContent {
            AppTheme {
                MyApp()
            }
        }
    }

    @Composable
    fun MainApp() {
        val viewmodel = koinViewModel<VideoViewModel>()

        val videoList by viewmodel.videoFiles.collectAsState()

        val navController = rememberNavController()

        NavHost(navController, Destination.VideoList) {

            composable<Destination.VideoList> {
                VideoListScreen(videoList) {
                    navController.navigate(Destination.VideoPlayer(it))
                }
            }

            composable<Destination.VideoPlayer> {
                val route = it.toRoute<Destination.VideoPlayer>()
                val player = ExoPlayer.Builder(applicationContext).build()
                MyPlayer(route.uri.toUri(),player=player)

            }

        }

    }


    @Composable
    fun MyApp() {
        val context = LocalContext.current
        val activity = LocalContext.current as Activity

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            android.Manifest.permission.READ_MEDIA_VIDEO
        else
            android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionState = remember {
            mutableStateOf(
                ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            )
        }
        val shouldShowRationale = remember {
            mutableStateOf(
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission
                )
            )
        }

        if (permissionState.value) {
            MainApp()
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (shouldShowRationale.value) {
                        Text(
                            text = "We need access to your video files to proceed.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Button(onClick = {
                        if (shouldShowRationale.value) {
                            ActivityCompat.requestPermissions(activity, arrayOf(permission), 100)
                            shouldShowRationale.value=false
                        } else {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                           startActivity(intent)
                        }
                    }) {
                        Text("Grant Permission")
                    }
                }
            }
        }

        // Handle permission result



        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                permissionState.value = result[permission] == true
                shouldShowRationale.value =
                    !permissionState.value && ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        permission
                    )
            }

        DisposableEffectLauncher(launcher, permission)

    }


    @OptIn(UnstableApi::class)
    @Composable
    fun MyPlayer(uri: Uri,player: ExoPlayer) {



        var isBuffering by remember { mutableStateOf(true) }
        val fullScreen by remember { mutableStateOf(false) }

        var lifeCycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }


        //? for local video or video url
//        val url = "https://www.sample-videos.com/video321/mp4/720/big_buck_bunny_720p_30mb.mp4"
//        val localUrl = "android.resource://${application.packageName}/${R.raw.sample}"


        val liveStreamingUrl =
            "https://live.relentlessinnovations.net:1936/imantv/imantv/playlist.m3u8"
        val mediaItem = MediaItem.fromUri(uri)

        //? media source for online video url
        val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
            .createMediaSource(mediaItem)

        //? media source for live streaming url
        val hlsMediaSource =
            HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(mediaItem)


        LaunchedEffect(Unit) {

         player.apply {
             stop()
             clearMediaItems()
              setMediaItem(mediaItem)
           // setMediaSource(hlsMediaSource)
            playWhenReady = true
            prepare()
        }
        }








        Box {


            AndroidView(
                {
                    PlayerView(it).also { playerView ->
                        playerView.player = player
                    }
                },
                if (fullScreen) Modifier.fillMaxSize() else Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )

            AnimatedVisibility(
                isBuffering,
                Modifier.align(Alignment.Center)
            ) { CircularProgressIndicator() }


        }





        DisposableEffect(Unit) {

            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    isBuffering = playbackState == Player.STATE_BUFFERING
                }
            }
            player.addListener(listener)

            onDispose {
                player.stop()
                player.release()
                player.removeListener(listener)
            }
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {

            val observer = LifecycleEventObserver { _, event ->
                lifeCycle = event
            }
            lifecycleOwner.lifecycle.addObserver(observer)


            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
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


@Composable
fun DisposableEffectLauncher(
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    permission: String,
) {
    DisposableEffect(Unit) {
        launcher.launch(arrayOf(permission))
        onDispose {}
    }
}


