package com.mc7.mystoryapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.mc7.mystoryapp.data.local.room.database.StoryDatabase
import com.mc7.mystoryapp.data.paging.StoryRemoteMediator
import com.mc7.mystoryapp.data.remote.response.DetailStoryResponse
import com.mc7.mystoryapp.data.remote.response.ErrorResponse
import com.mc7.mystoryapp.data.remote.response.LoginResponse
import com.mc7.mystoryapp.data.remote.response.StoriesResponse
import com.mc7.mystoryapp.data.remote.response.StoryItem
import com.mc7.mystoryapp.data.remote.retrofit.ApiService
import com.mc7.mystoryapp.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) : StoryRepository {

    override fun registerUser(
        name: String, email: String, password: String
    ): LiveData<Result<ErrorResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerUser(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    override fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.loginUser(email, password)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(): LiveData<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoryRemoteMediator(apiService, storyDatabase),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
    }

    override fun getStoriesWithLocation(): LiveData<Result<StoriesResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStoriesWithLocation()
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    override fun getDetailStory(id: String): LiveData<Result<DetailStoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getDetailStory(id)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    override fun uploadStory(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody,
        lat: Float?,
        lon: Float?
    ): LiveData<Result<ErrorResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadStory(
                multipartBody,
                requestBody,
                lat,
                lon
            )

            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse =
                Gson().fromJson(errorBody, ErrorResponse::class.java)

            emit(Result.Error(errorResponse.message.toString()))
        }
    }
}