package com.mc7.mystoryapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.mc7.mystoryapp.BuildConfig
import com.mc7.mystoryapp.data.StoryPreference
import com.mc7.mystoryapp.data.StoryRepository
import com.mc7.mystoryapp.data.StoryRepositoryImpl
import com.mc7.mystoryapp.data.dataStore
import com.mc7.mystoryapp.data.local.room.dao.StoryDao
import com.mc7.mystoryapp.data.local.room.database.StoryDatabase
import com.mc7.mystoryapp.data.remote.retrofit.ApiService
import com.mc7.mystoryapp.utils.MyApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideMyAppContext(@ApplicationContext context: Context): MyApp {
        return context as MyApp
    }

    @Provides
    fun provideApiService(preference: StoryPreference): ApiService {
        val token: String = runBlocking {
            preference.getUserToken().first()
        }

        val loggingInterceptor = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(requestHeaders)
        }

        val client = if (token == "nothing") {
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideStoryDatabase(appContext: MyApp): StoryDatabase {
        return Room.databaseBuilder(
            appContext,
            StoryDatabase::class.java, "story.db"
        )
            .build()
    }

    @Provides
    fun provideStoryDao(storyDatabase: StoryDatabase): StoryDao {
        return storyDatabase.storyDao()
    }

    @Provides
    fun provideStoryRepository(
        apiService: ApiService,
        storyDatabase: StoryDatabase
    ): StoryRepository {
        return StoryRepositoryImpl(apiService, storyDatabase)
    }

    @Provides
    fun provideDataStorePreferences(appContext: MyApp): DataStore<Preferences> {
        return appContext.dataStore
    }

    @Provides
    fun provideStoryPreference(dataStore: DataStore<Preferences>): StoryPreference {
        return StoryPreference(dataStore)
    }
}