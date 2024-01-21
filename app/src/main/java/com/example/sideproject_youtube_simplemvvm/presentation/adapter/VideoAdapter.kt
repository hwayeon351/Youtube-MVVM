package com.example.sideproject_youtube_simplemvvm.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sideproject_youtube_simplemvvm.R
import com.example.sideproject_youtube_simplemvvm.data.model.VideoModel
import com.example.sideproject_youtube_simplemvvm.databinding.ItemVideoBinding

class VideoAdapter(val callback: (VideoModel) -> Unit) :
    ListAdapter<VideoModel, VideoAdapter.ViewHolder>(diffUtil) {
    private lateinit var binding: ItemVideoBinding

    inner class ViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoModel) {
            Log.d(TAG,"$TAG::ViewHolder.bind()")
            binding.titleTextView.text = item.title
            binding.subtitleTextView.text = item.subtitle

            Glide.with(binding.thumbnailImageView.context)
                .load(item.thumb)
                .into(binding.thumbnailImageView)

            binding.root.setOnClickListener {
                callback(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG,"$TAG::onCreateViewHolder()")
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_video, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG,"$TAG::onBindViewHolder()")
        holder.bind(currentList[position])
    }

    companion object {
        private const val TAG = "VideoAdapter"

        val diffUtil = object : DiffUtil.ItemCallback<VideoModel>() {
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                Log.d(TAG,"$TAG::DiffUtil.ItemCallback.areItemsTheSame()")
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                Log.d(TAG,"$TAG::DiffUtil.ItemCallback.areContentsTheSame()")
                return oldItem.sources == newItem.sources
            }

        }
    }
}