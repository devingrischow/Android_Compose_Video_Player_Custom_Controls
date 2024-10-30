package com.example.android_compose_video_player_custom_controls.data.view_model

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.ExoPlayer
import com.example.android_compose_video_player_custom_controls.buildCache
import com.example.android_compose_video_player_custom_controls.createExoPlayer
import com.example.android_compose_video_player_custom_controls.data.model.VideoPlayerDataModel


@OptIn(UnstableApi::class)
class VideoPlayerViewMode
    (context: Context): ViewModel() {

    private val _videoPlayerData = mutableStateOf(VideoPlayerDataModel(context = context ) )
    val videoPlayerData = _videoPlayerData



    fun buildVideo(){

        Log.d("VIDEO_LOG", "DATA MODEL PRE BUILDING: ${_videoPlayerData.value}")

        //FIRST, Build Cache
        _videoPlayerData.value.cacheSystem = buildCache(context = _videoPlayerData.value.context)

        //RESULT OF CACHE:
        Log.d("VIDEO_LOG", "Result Of Cache: ${_videoPlayerData.value}")

        //NEXT,
        //Build the player
        //Force cache because it should be good
        _videoPlayerData.value.exoPlayer = createExoPlayer(_videoPlayerData.value.context, _videoPlayerData.value.cacheSystem!!, "https://firebasestorage.googleapis.com/v0/b/easyeats-43b0d.appspot.com/o/Recipe_Steps%2FEasy%20Chicken%20Caesar%20Salad%2FeasyChickenCaesarSalad_Step2.mp4?alt=media&token=7e070cc3-bf82-43d2-ab99-6865b81d14f8")
        Log.d("VIDEO_LOG", "After building Exo Player: ${_videoPlayerData.value}")

    }

    //Create function for create expo player to use internal value of cache

    init {
        Log.d("VIDEO_LOG", "Current Context: $context")

        //create cache system
        val cacheSystem = buildCache(context)
        Log.d("VIDEO_LOG", "Created Cache: $cacheSystem")
        //Set cache to internal system
        _videoPlayerData.value.cacheSystem = cacheSystem
        Log.d("VIDEO_LOG", "Set Value of cache System: ${_videoPlayerData.value}")


        //create new exo player
        val exoPlayer = createExoPlayer(cache = _videoPlayerData.value.cacheSystem!!, context = context, videoUrl =  "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        Log.d("VIDEO_LOG", "Created ExoPlayer: $exoPlayer")

        //Set Exo Player
        _videoPlayerData.value.exoPlayer = exoPlayer
        Log.d("VIDEO_LOG", "Set Value of exoplayer System: ${_videoPlayerData.value}")
    }


}