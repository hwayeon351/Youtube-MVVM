package com.example.sideproject_youtube_simplemvvm.presentation.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sideproject_youtube_simplemvvm.R
import com.example.sideproject_youtube_simplemvvm.databinding.FragmentPlayerBinding
import com.example.sideproject_youtube_simplemvvm.presentation.adapter.VideoAdapter
import com.example.sideproject_youtube_simplemvvm.presentation.viewmodel.MainViewModel
import com.example.sideproject_youtube_simplemvvm.utils.Status
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlin.math.abs

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var videoAdapter: VideoAdapter
    private var player: SimpleExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "$TAG::onCreateView()")
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "$TAG::onViewCreated()")
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        initMotionLayoutEvent()
        initRecyclerView()
        initPlayer()
        initControlButton()
    }

    private fun setObservers() {
        mainViewModel.getPlayerModel().observe(viewLifecycleOwner) {
            Log.d(TAG, "${TAG} Observer Triggered by PlayerModel!!")
            player ?: return@observe

            if (it.isPlaying) player?.play()
            else player?.pause()

            binding.bottomPlayerControlButton.setImageResource(it.buttonImg)
            binding.startTitleTextView.text = it.videoModel.title
            binding.endTitleTextView.text = it.videoModel.title
            binding.descriptionTitleTextView.text = it.videoModel.description
        }

        Log.d(TAG,"setObservers()")
        //SingleVideoList Observing
        mainViewModel.getSingleVideoList().observe(this) {
            Log.d(TAG, "${TAG} Observer Triggered by SingleVideoList!!")
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
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        //SeriesVideoList Observing
        mainViewModel.getSeriesVideoList().observe(this) {
            Log.d(TAG, "${TAG} Observer Triggered by SeriesVideoList!!")
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
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        //ParallelVideoList Observing
        mainViewModel.getParallelVideoList().observe(this) {
            Log.d(TAG, "${TAG} Observer Triggered by ParallelVideoList!!")
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
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun initMotionLayoutEvent() {
        Log.d(TAG, "$TAG::initMotionLayoutEvent()")

        binding.playerMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                Log.d(TAG, "$TAG::MotionLayout.onTransitionChange()")
                binding?.let {
                    (activity as MainActivity).also { mainActivity ->
                        mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress =
                            abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {}

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })
    }

    private fun initRecyclerView() {
        Log.d(TAG, "$TAG::initRecyclerView()")

        videoAdapter = VideoAdapter(callback = { videoModel ->
            mainViewModel.onVideoItemClick(videoModel)
            preparePlayer(videoModel.sources)
        })

        binding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayer() {
        Log.d(TAG, "$TAG::initPlayer()")

        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }

        binding.playerView.let {
            it.player = player
            it.useController = false
        }
    }

    private fun initControlButton() {
        Log.d(TAG, "$TAG::initControlButton()")

        binding.bottomPlayerControlButton.setOnClickListener {
            Log.d(TAG, "bottomPlayerControlButton onClicked!!")
            if (player == null) return@setOnClickListener
            mainViewModel.onPlayerControlButtonClick()
        }
    }

    fun preparePlayer(url: String) {
        Log.d(TAG, "$TAG::preparePlayer()")

        context?.let { it ->
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player?.let { player ->
                player.setMediaSource(mediaSource)
                player.prepare()
            }
        }

        binding.let {
            it.playerMotionLayout.transitionToEnd()
        }
    }

    override fun onStop() {
        Log.d(TAG, "$TAG::onStop()")
        super.onStop()

        player?.pause()
    }

    override fun onDestroy() {
        Log.d(TAG, "$TAG::onDestroy()")
        super.onDestroy()

        _binding = null
        player?.release()
    }

    companion object {
        private const val TAG = "PlayerFragment"
    }
}