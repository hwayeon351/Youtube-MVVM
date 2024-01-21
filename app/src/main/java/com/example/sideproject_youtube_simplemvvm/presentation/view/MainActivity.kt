package com.example.sideproject_youtube_simplemvvm.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sideproject_youtube_simplemvvm.R
import com.example.sideproject_youtube_simplemvvm.databinding.ActivityMainBinding
import com.example.sideproject_youtube_simplemvvm.presentation.adapter.VideoAdapter
import com.example.sideproject_youtube_simplemvvm.presentation.viewmodel.MainViewModel
import com.example.sideproject_youtube_simplemvvm.utils.Status

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"$TAG::onCreate()")
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        setObservers()
        initPlayerFragment()
        initVideoListRecyclerView()
    }

    private fun setObservers() {
        Log.d(TAG,"$TAG::setObservers()")

        /** SingleVideoList Observing */
        mainViewModel.getSingleVideoList().observe(this) {
            Log.d(TAG, "$TAG Observer Triggered by SingleVideoList!!")
            when(it.status) {
                Status.LOADING -> {
                    binding.progressBar?.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    it.data?.let { videoDto ->
                        videoAdapter.submitList(videoDto.videos)
                    }
                    binding.progressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        /** SeriesVideoList Observing */
        mainViewModel.getSeriesVideoList().observe(this) {
            Log.d(TAG, "$TAG Observer Triggered by SeriesVideoList!!")
            when(it.status) {
                Status.LOADING -> {
                    binding.progressBar?.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    it.data?.let { videoDto ->
                        videoAdapter.submitList(videoDto.videos)
                    }
                    binding.progressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        /** ParallelVideoList Observing 이하 동일... */
        mainViewModel.getParallelVideoList().observe(this) {
            Log.d(TAG, "$TAG Observer Triggered by ParallelVideoList!!")
            when(it.status) {
                Status.LOADING -> {
                    binding.progressBar?.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    it.data?.let { videoDto ->
                        videoAdapter.submitList(videoDto.videos)
                    }
                    binding.progressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun initPlayerFragment() {
        Log.d(TAG,"$TAG::initPlayerFragment()")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlayerFragment())
            .commit()
    }

    private fun initVideoListRecyclerView() {
        Log.d(TAG,"$TAG::initVideoListRecyclerView()")
        videoAdapter = VideoAdapter(callback = { videoModel ->
            mainViewModel.onVideoItemClick(videoModel)
            supportFragmentManager.fragments.find { it is PlayerFragment }?.let {
                (it as PlayerFragment).preparePlayer(videoModel.sources)
            }
        })

        binding?.mainRecyclerView?.let {
            it.adapter = videoAdapter
            it.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onDestroy() {
        Log.d(TAG,"$TAG::onDestroy()")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}