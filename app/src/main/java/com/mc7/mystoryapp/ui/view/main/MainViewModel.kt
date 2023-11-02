package com.mc7.mystoryapp.ui.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mc7.mystoryapp.data.StoryRepository
import com.mc7.mystoryapp.data.remote.response.StoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {
    val storyItems: LiveData<PagingData<StoryItem>> =
        repository.getStories().cachedIn(viewModelScope)
}