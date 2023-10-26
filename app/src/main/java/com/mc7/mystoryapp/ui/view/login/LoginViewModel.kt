package com.mc7.mystoryapp.ui.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc7.mystoryapp.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: StoryRepository
): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<String?>()
    val isSuccess: LiveData<String?> = _isSuccess

    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    fun isLoading(isLoadingYa: Boolean) {
        _isLoading.value = isLoadingYa
    }

    fun isSuccess(isSuccessYa: String?) {
        _isSuccess.value = isSuccessYa
    }

    fun isError(isErrorYa: String?) {
        _isError.value = isErrorYa
    }

    fun loginUser(email: String, password: String) =
        repository.loginUser(email, password)
}