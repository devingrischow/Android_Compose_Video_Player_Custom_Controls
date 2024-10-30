package com.example.android_compose_video_player_custom_controls.data.model

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.ExoPlayer

@OptIn(UnstableApi::class)
data class VideoPlayerDataModel(

    var context:Context,


    var cacheSystem:Cache? = null,


    var exoPlayer:ExoPlayer? = null,

    )