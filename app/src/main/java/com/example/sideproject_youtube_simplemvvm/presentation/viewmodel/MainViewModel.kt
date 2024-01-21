package com.example.sideproject_youtube_simplemvvm.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.sideproject_youtube_simplemvvm.data.Repository
import com.example.sideproject_youtube_simplemvvm.data.dto.VideoDto
import com.example.sideproject_youtube_simplemvvm.data.model.PlayerModel
import com.example.sideproject_youtube_simplemvvm.data.model.VideoModel
import com.example.sideproject_youtube_simplemvvm.utils.Resource
import com.example.sideproject_youtube_simplemvvm.utils.Status
import com.example.sideproject_youtube_simplemvvm.utils.Utils
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private var _singleVideoList = MutableLiveData<Resource<VideoDto>>()
    private val singleVideoList: LiveData<Resource<VideoDto>>
        get() = _singleVideoList

    private var _seriesVideoList = MutableLiveData<Resource<VideoDto>>()
    private val seriesVideoList: LiveData<Resource<VideoDto>>
        get() = _seriesVideoList

    private var _parallelVideoList = MutableLiveData<Resource<VideoDto>>()
    private val parallelVideoList: LiveData<Resource<VideoDto>>
        get() = _parallelVideoList

    private var _playerModel = MutableLiveData<PlayerModel>()
    private val playerModel: LiveData<PlayerModel>
        get() = _playerModel

    init {
        //Single Request
        fetchSingleVideoList()

        //Series Request
        //fetchSeriesVideoList()

        //Parallel Request
        //fetchParallelVideoList()
    }

    fun fetchSingleVideoList() = viewModelScope.launch {
        Log.d(TAG, "$TAG::fetchSingleVideoList()")

        /** STATUS.LOADING 상태: Request 처리 중 */
        _singleVideoList.value = Resource.loading(null)

        /** STATUS.SUCCESS 상태: Request 성공
                        or
            STATUS.ERROR 상태: Request 실패 */
        _singleVideoList.value = Repository.requestVideoList()
    }

    fun fetchSeriesVideoList() = viewModelScope.launch {
        Log.d(TAG, "$TAG::fetchSeriesVideoList()")

        /** STATUS.LOADING 상태: Request 처리 중 */
        _seriesVideoList.value = Resource.loading(null)
        val res1 = Repository.requestVideoList()
        val res2 = Repository.requestVideoList()

        /** STATUS.SUCCESS 상태: Request 성공 */
        if (res1.status == Status.SUCCESS && res2.status == Status.SUCCESS) {
            val allRes = mutableListOf<VideoModel>()
            res1.data?.let { allRes.addAll(it.videos) }
            res2.data?.let { allRes.addAll(it.videos) }

            _seriesVideoList.value = Resource.success(VideoDto(allRes))
        }

        /** STATUS.ERROR 상태: 하나 이상의 Request 실패 */
        else {
            var error = res1.message + "\n" + res2.message
            _seriesVideoList.value = Resource.error(msg = error, data = null)
        }
    }

    fun fetchParallelVideoList() = viewModelScope.launch {
        Log.d(TAG, "$TAG::fetchParallelVideoList()")

        /** STATUS.LOADING 상태: Request 처리 중 */
        _parallelVideoList.value = Resource.loading(null)

        val resDeffered1 = async { Repository.requestVideoList() }
        val resDeffered2 = async { Repository.requestVideoList() }

        val res1 = resDeffered1.await()
        val res2 = resDeffered2.await()

        /** STATUS.SUCCESS 상태: Request 성공 */
        if (res1.status == Status.SUCCESS && res2.status == Status.SUCCESS) {
            val allRes = mutableListOf<VideoModel>()
            res1.data?.let { allRes.addAll(it.videos) }
            res2.data?.let { allRes.addAll(it.videos) }

            _parallelVideoList.value = Resource.success(VideoDto(allRes))
        }

        /** STATUS.ERROR 상태: 하나 이상의 Request 실패 */
        else {
            var error = res1.message + "\n" + res2.message
            _parallelVideoList.value = Resource.error(msg = error, data = null)
        }
    }

    fun onVideoItemClick(videoModel: VideoModel) {
        Log.d(TAG, "$TAG::onVideoItemClick()")
        _playerModel.value = PlayerModel(videoModel, true, Utils.PAUSE_ICON)
    }

    fun onPlayerControlButtonClick() {
        Log.d(TAG, "$TAG::onPlayerControlButtonClick()")
        _playerModel.value = _playerModel.value?.also {
            when (it.isPlaying) {
                true -> {
                    it.isPlaying = false
                    it.buttonImg = Utils.PLAY_ICON
                }
                else -> {
                    it.isPlaying = true
                    it.buttonImg = Utils.PAUSE_ICON
                }
            }
        }
    }

    @JvmName("getVideoList1")
    fun getSingleVideoList(): LiveData<Resource<VideoDto>> = singleVideoList

    @JvmName("getSeriesVideoList1")
    fun getSeriesVideoList(): LiveData<Resource<VideoDto>> = seriesVideoList

    @JvmName("getParallelVideoList1")
    fun getParallelVideoList(): LiveData<Resource<VideoDto>> = parallelVideoList

    @JvmName("getPlayerModel1")
    fun getPlayerModel(): LiveData<PlayerModel> = playerModel

    companion object {
        private const val TAG = "MainViewModel"
    }
}