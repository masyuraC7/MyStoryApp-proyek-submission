package com.mc7.mystoryapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mc7.mystoryapp.data.local.entity.RemoteKeys
import com.mc7.mystoryapp.data.local.room.database.StoryDatabase
import com.mc7.mystoryapp.data.remote.response.StoryItem
import com.mc7.mystoryapp.data.remote.retrofit.ApiService
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) : RemoteMediator<Int, StoryItem>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)

                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                nextKey
            }
        }

        try {
            val responseData = apiService.getStories(page, state.config.pageSize)
            val endOfPaginationReached = responseData.listStory.isEmpty()

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.remoteKeysDao().deleteAll()
                    storyDatabase.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                storyDatabase.remoteKeysDao().insertAll(keys)
                storyDatabase.storyDao().insert(responseData.listStory)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {

            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, StoryItem>
    ): RemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, StoryItem>
    ): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, StoryItem>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}