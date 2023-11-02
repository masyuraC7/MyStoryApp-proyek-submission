package com.mc7.mystoryapp.ui.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.mc7.mystoryapp.DataDummy
import com.mc7.mystoryapp.MainDispatcherRule
import com.mc7.mystoryapp.data.StoryRepository
import com.mc7.mystoryapp.data.remote.response.StoryItem
import com.mc7.mystoryapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryItem()
        val data: PagingData<StoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedQuote = MutableLiveData<PagingData<StoryItem>>()

        expectedQuote.value = data

        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(storyRepository)
        val actualQuote: PagingData<StoryItem> = mainViewModel.storyItems.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualQuote)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<StoryItem>>()

        expectedQuote.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(storyRepository)
        val actualQuote: PagingData<StoryItem> = mainViewModel.storyItems.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualQuote)
        assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryItem>>>() {
    override fun getRefreshKey(
        state: PagingState<Int, LiveData<List<StoryItem>>>
    ): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>):
            LoadResult<Int, LiveData<List<StoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<StoryItem>): PagingData<StoryItem> {
            return PagingData.from(items)
        }
    }
}