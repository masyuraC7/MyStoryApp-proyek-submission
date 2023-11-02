package com.mc7.mystoryapp.ui.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mc7.mystoryapp.data.StoryPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferences: StoryPreference
): ViewModel() {

    fun getUserToken(): LiveData<String> {
        return preferences.getUserToken().asLiveData()
    }

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            preferences.saveUserToken(token)
        }
    }

    fun deleteUserToken() {
        viewModelScope.launch {
            preferences.deleteUserToken()
        }
    }
}