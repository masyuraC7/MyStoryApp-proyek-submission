package com.mc7.mystoryapp.ui.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc7.mystoryapp.data.StoryRepository
import com.mc7.mystoryapp.data.remote.response.StoryItem
import com.mc7.mystoryapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: StoryRepository
):ViewModel() {
    private val _isFillStories = MutableLiveData<List<StoryItem>?>()
    val isFillStories: LiveData<List<StoryItem>?> = _isFillStories

    init {
        getStoriesWithLocation()
    }

    private fun getStoriesWithLocation() {
        repository.getStoriesWithLocation().observeForever { result ->
            when (result) {
                is Result.Loading -> {}

                is Result.Success -> {
                    _isFillStories.value = result.data.listStory
                }

                is Result.Error -> {}
            }
        }
    }
}