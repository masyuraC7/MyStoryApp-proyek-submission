package com.mc7.mystoryapp.ui.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc7.mystoryapp.data.StoryRepository
import com.mc7.mystoryapp.data.remote.response.StoryItem
import com.mc7.mystoryapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    private val _isFillStories = MutableLiveData<List<StoryItem>?>()
    val isFillStories: LiveData<List<StoryItem>?> = _isFillStories

    init {
        getStories()
    }

    private fun getStories() {
        repository.getStories().observeForever { result ->
            when (result) {
                is Result.Loading -> {
                    _isLoading.value = true
                }

                is Result.Success -> {
                    _isLoading.value = false
                    _isError.value = null
                    _isFillStories.value = result.data.listStory
                }

                is Result.Error -> {
                    _isLoading.value = false
                    _isFillStories.value = null
                    val msgError = result.error
                    _isError.value = msgError
                }
            }
        }
    }
}