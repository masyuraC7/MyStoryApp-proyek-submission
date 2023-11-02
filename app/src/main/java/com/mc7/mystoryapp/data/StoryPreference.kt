package com.mc7.mystoryapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "story")

class StoryPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val userToken = stringPreferencesKey("userToken")

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[userToken] ?: "nothing"
        }
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[userToken] = token
        }
    }

    suspend fun deleteUserToken() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}