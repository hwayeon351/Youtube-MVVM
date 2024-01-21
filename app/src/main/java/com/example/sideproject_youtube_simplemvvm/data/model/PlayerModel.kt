package com.example.sideproject_youtube_simplemvvm.data.model

import com.example.sideproject_youtube_simplemvvm.utils.Utils

data class PlayerModel(
    var videoModel: VideoModel,
    var isPlaying: Boolean = false,
    var buttonImg: Int = Utils.PLAY_ICON
)