package com.mc7.mystoryapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mc7.mystoryapp.data.local.room.database.StoryDatabase
import com.mc7.mystoryapp.data.remote.response.DetailStoryResponse
import com.mc7.mystoryapp.data.remote.response.ErrorResponse
import com.mc7.mystoryapp.data.remote.response.LoginResponse
import com.mc7.mystoryapp.data.remote.response.StoriesResponse
import com.mc7.mystoryapp.data.remote.response.StoryItem
import com.mc7.mystoryapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockApi,
            mockDb,
        )

        val pagingState = PagingState<Int, StoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): ErrorResponse {
        return ErrorResponse()
    }

    override suspend fun loginUser(email: String, password: String): LoginResponse {
        return LoginResponse()
    }

    override suspend fun getStories(page: Int, size: Int): StoriesResponse {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryItem(
                "",
                "",
                "",
                "",
                0.0,
                i.toString(),
                0.0
            )
            items.add(quote)
        }

        return StoriesResponse(
            items.subList((page - 1) * size, (page - 1) * size + size),
            false,
            ""
        )
    }

    override suspend fun getStoriesWithLocation(location: Int): StoriesResponse {
        return StoriesResponse(
            arrayListOf(),
            false,
            ""
        )
    }

    override suspend fun getDetailStory(id: String): DetailStoryResponse {
        return DetailStoryResponse()
    }

    override suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): ErrorResponse {
        return ErrorResponse()
    }
}