package com.mc7.mystoryapp.ui.view.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mc7.mystoryapp.data.remote.response.StoryItem
import com.mc7.mystoryapp.databinding.ItemListStoryBinding
import com.mc7.mystoryapp.ui.view.detail.DetailStoryActivity
import com.mc7.mystoryapp.utils.DateFormatter

class ListStoryAdapter :
    PagingDataAdapter<StoryItem, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bind(user)
        }
        holder.itemView.setOnClickListener {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                Pair(holder.binding.imgStory, "img_story"),
                Pair(holder.binding.txtReviewer, "txt_reviewer"),
                Pair(holder.binding.createdAt, "txt_created_at"),
                Pair(holder.binding.txtTitle, "txt_title")
            ).toBundle()

            val intentToDetailActivity =
                Intent(holder.itemView.context, DetailStoryActivity::class.java)
            intentToDetailActivity.putExtra("extra_id", user?.id)
            holder.itemView.context.startActivity(intentToDetailActivity, optionsCompat)
        }
    }

    class MyViewHolder(val binding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(story: StoryItem) {
            binding.txtTitle.text = story.name
            binding.txtReviewer.text = story.name

            val getDate = DateFormatter.formatDate(story.createdAt.toString())
            val finalDate = "Uploaded at $getDate"

            binding.createdAt.text = finalDate
            Glide.with(binding.root)
                .load(story.photoUrl)
                .into(binding.imgStory)
                .clearOnDetach()
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryItem,
                newItem: StoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
