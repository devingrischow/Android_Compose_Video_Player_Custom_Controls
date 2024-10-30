package com.example.android_compose_video_player_custom_controls

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.android_compose_video_player_custom_controls.data.view_model.VideoPlayerViewMode
import com.example.android_compose_video_player_custom_controls.ui.theme.Android_Compose_Video_Player_Custom_ControlsTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            MainPlayerScreen()

        }
    }
}


//@Composable
//fun HorizontalPlayerScreen


@Composable
fun MainPlayerScreen(){
    //http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4
    //Declare video source outside




    val scrollState = rememberScrollState()


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
//        Spacer(modifier = Modifier.weight(1f) )

        VideoPlayerZone()


        Text(text = "This is a VIDEO PLAYER!",
            color = Color.Blue,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.weight(1f) )
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerZone(){

    val context = LocalContext.current

    var didLoadData by rememberSaveable {
        mutableStateOf(false)
    }

    // Build cache for caching the media
//    val cacheSystem = remember { buildCache(context) }

    //View Model Player
    val videoViewModel = viewModel {
        VideoPlayerViewMode(context = context)
    }
    //.videoPlayerData.value = VideoPlayerViewModel().videoPlayerData.value.copy(exoPlayer = createExoPlayer(context, cacheSystem, videoUrl), cacheSystem = buildCache(context) )

//    val exoPlayer = remember { createExoPlayer(context, cacheSystem, videoUrl) }

    val buttonText = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Log.d("VIDEO_LOG", "Current Value Of play when ready ${videoViewModel.videoPlayerData.value.exoPlayer!!.playWhenReady}")

        buttonText.value = if (videoViewModel.videoPlayerData.value.exoPlayer?.playWhenReady == true){
            "Pause!"
        }else{
            "Un-Pause"
        }
    }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        //Android View to HOUSE the PLAYER
        AndroidView(
            factory = {
                //Use a SURFACE to house the view player
                SurfaceView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        //Width
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        //height
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    //After applying Layout Params, Attach the exo player to the surface view
                    videoViewModel.videoPlayerData.value.exoPlayer?.setVideoSurfaceView(this)
                }//Bottom of Factory
            },
            //Modifiers
            modifier = Modifier

                .aspectRatio(1080f / 1920f)
                .fillMaxWidth()
        )

        //Pause Button
        //Right Now Disabled
        Button(onClick = {

            //if video is playing, pause it,
            //if its not, unapause it

            if (videoViewModel.videoPlayerData.value.exoPlayer?.playWhenReady == true){
                //pause it
                videoViewModel.videoPlayerData.value.exoPlayer?.pause()
            }else {
                //Unpause
                videoViewModel.videoPlayerData.value.exoPlayer?.play()
            }

            Log.d("Pressed_Button_VIDEO_LOG", "AFTER SETTING VALUE: Pressed Pause/Play Button: ButtonState: ${videoViewModel.videoPlayerData.value.exoPlayer?.playWhenReady}")

            buttonText.value = if (videoViewModel.videoPlayerData.value.exoPlayer?.playWhenReady == true){
                "Pause!"
            }else{
                "Un-Pause"
            }

            Log.d("Pressed_Button_VIDEO_LOG", "Set Text")



        } ) {
            Text(buttonText.value)
            //Bottom Of Button
        }

    }




}


@OptIn(UnstableApi::class)
fun createExoPlayer(context:Context, cache: Cache, videoUrl: String):ExoPlayer {
    //Create a new exoPlayer Instance
    return ExoPlayer.Builder(context).build().apply {

        //Set up the MediaSource Cache DataSource
        val dataSourceFactory = buildDataSourceFactory(context, cache)
        //Using a Progressive media source, it uses the factory and the data source to introduce the custom cache, when,
        // if available plays the video from cache, or downloads and plays it and then plays it through cache

        val cachedMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUrl))
            .mediaItem


//        // Add a media item to play (URL, asset, etc.)
//        val mediaItem = MediaItem.fromUri(videoUrl)
        setMediaItem(cachedMediaSource)

        //set to repeat infinitely mode
        repeatMode = Player.REPEAT_MODE_ALL


        //prepare and start the playback
        prepare()
        play()
    }
}



//Cache Builder Function
@OptIn(UnstableApi::class)
fun buildCache(context: Context):SimpleCache {
    //Declare the directory the cache will use
    //Required to state the specific cache location on Android
    val cacheDir = File(context.cacheDir, "media")

    //2 Constants
    val KILOBYTESINMEGABYTES = 1024L
    val BYTESINKILOBYTES = 1024L

    val desiredCacheSizeMegaBytes = 100L

    val cacheSize = desiredCacheSizeMegaBytes * KILOBYTESINMEGABYTES * BYTESINKILOBYTES

    //Declare the Type of Policy to use for cache removal
    //An evictor policy
    val evictor = LeastRecentlyUsedCacheEvictor(cacheSize)
    //Automatically evict the oldest items, not letting size exceed the given megabytes

    return SimpleCache(cacheDir, evictor, StandaloneDatabaseProvider(context))

}


@OptIn(UnstableApi::class)
fun buildDataSourceFactory(context: Context, cache:Cache): DefaultDataSource.Factory {
    val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)//set the cache to the cache source factory
        .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))

    return DefaultDataSource.Factory(context, cacheDataSourceFactory)
}