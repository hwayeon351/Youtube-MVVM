package com.example.sideproject_youtube_simplemvvm.data

import android.util.Log
import com.example.sideproject_youtube_simplemvvm.data.dto.VideoDto
import com.example.sideproject_youtube_simplemvvm.data.service.RetrofitClient
import com.example.sideproject_youtube_simplemvvm.utils.Resource

object Repository {
    private const val TAG = "Repository"

    suspend fun requestVideoList(): Resource<VideoDto> {
        return try {
            val response = RetrofitClient.videoService.listVideos()
            if (response.isSuccessful) {
                Log.d(TAG, "$TAG::requestVideoList() Response Success!! ${response.body().toString()}")
                Resource.success(response.body())
            } else {
                Log.d(TAG, "$TAG::requestVideoList() Response Error!! ${response.errorBody().toString()}")
                Resource.error(msg = response.errorBody().toString(), data = null)
            }
        } catch (e: Exception) {
            Log.d(TAG, "$TAG::requestVideoList() Request Fail!! ${e.message.toString()}")
            Resource.error(msg = e.message.toString(), data = null)
        }
    }
}