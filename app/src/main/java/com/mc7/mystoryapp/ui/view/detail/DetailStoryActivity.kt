package com.mc7.mystoryapp.ui.view.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mc7.mystoryapp.R
import com.mc7.mystoryapp.data.remote.response.StoryItem
import com.mc7.mystoryapp.databinding.ActivityDetailStoryBinding
import com.mc7.mystoryapp.utils.DateFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.topAppBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        val id = intent.getStringExtra(EXTRA_ID)

        viewModel.getStory(id.toString())

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isError.observe(this) {
            showError(it)
        }

        viewModel.isFillStories.observe(this){
            showDetailStory(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDetailStory(it: StoryItem?) {
        if (it != null) {
            Glide.with(this).load(it.photoUrl).into(binding.imgStory).clearOnDetach()
            binding.txtReviewer.text = it.name
            binding.txtCreatedAt.text = "Uploaded at "+ DateFormatter.formatDate(it.createdAt.toString())
            binding.txtDescription.text = it.description
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: String?) {
        binding.tvErrorMessage.visibility = if (isError != null) View.VISIBLE else View.GONE

        binding.tvErrorMessage.text = isError
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}