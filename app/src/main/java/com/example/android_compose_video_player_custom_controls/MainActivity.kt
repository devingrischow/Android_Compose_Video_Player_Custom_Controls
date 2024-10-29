package com.example.android_compose_video_player_custom_controls

import android.content.Context
import android.os.Bundle
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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.android_compose_video_player_custom_controls.ui.theme.Android_Compose_Video_Player_Custom_ControlsTheme

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
    //Declare video source outside
    var selectedVideoState by rememberSaveable {
        mutableStateOf<String>("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    }

    val scrollState = rememberScrollState()



    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
//        Spacer(modifier = Modifier.weight(1f) )

        VideoPlayerZone(selectedVideoState)


        Text(text = "This is a VIDEO PLAYER!",
            color = Color.Blue,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.weight(1f) )
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerZone(videoUrl:String){

    val context = LocalContext.current

    val exoPlayer = remember { createExoPlayer(context) }





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
                    exoPlayer.setVideoSurfaceView(this)
                }//Bottom of Factory
            },
            //Modifiers
            modifier = Modifier

                .aspectRatio(1080f/1920f)
                .fillMaxWidth()
        )

        //Pause Button
        //Right Now Disabled
        Button(onClick = {
            Log.d("Pressed Button", "Pressed")
        } ) {
            Text("Button")
            //Bottom Of Button
        }

    }




}



fun createExoPlayer(context:Context):ExoPlayer {
    //Create a new exoPlayer Instance
    return ExoPlayer.Builder(context).build().apply {
        // Add a media item to play (URL, asset, etc.)
        val mediaItem = MediaItem.fromUri("https://firebasestorage.googleapis.com/v0/b/easyeats-43b0d.appspot.com/o/Recipe_Steps%2FEasy%20Folded%20Over%20Cheese%20Quesadilla%2FeasyFoldedOverCheeseQuesadilla_Step4.mp4?alt=media&token=cf1a6c2c-2d50-4492-bb6f-57519225b57a")
        setMediaItem(mediaItem)

        //set to repeat infinitely mode
        repeatMode = Player.REPEAT_MODE_ALL

        //prepare and start the playback
        prepare()
        play()
    }
}