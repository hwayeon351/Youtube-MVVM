package com.example.sideproject_youtube_simplemvvm.data.service

import com.example.sideproject_youtube_simplemvvm.data.dto.VideoDto
import retrofit2.Response
import retrofit2.http.GET

interface VideoService {
    @GET("/videolist")
    suspend fun listVideos(): Response<VideoDto>
}