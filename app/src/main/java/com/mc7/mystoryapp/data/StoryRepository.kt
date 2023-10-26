package com.mc7.mystoryapp.data

import androidx.lifecycle.LiveData
import com.mc7.mystoryapp.data.remote.response.DetailStoryResponse
import com.mc7.mystoryapp.data.remote.response.LoginResponse
import com.mc7.mystoryapp.data.remote.response.ErrorResponse
import com.mc7.mystoryapp.data.remote.response.StoriesResponse
import com.mc7.mystoryapp.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StoryRepository {
    fun registerUser(name: String, email: String, password: String):
            LiveData<Result<ErrorResponse>>

    fun loginUser(email: String, password: String):
            LiveData<Result<LoginResponse>>

    fun getStories(): LiveData<Result<StoriesResponse>>

    fun getDetailStory(id: String): LiveData<Result<DetailStoryResponse>>

    fun uploadStory(multipartBody: MultipartBody.Part, requestBody: RequestBody):
            LiveData<Result<ErrorResponse>>
}