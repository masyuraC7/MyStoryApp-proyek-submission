package com.mc7.mystoryapp.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mc7.mystoryapp.R
import com.mc7.mystoryapp.ui.view.login.AuthViewModel
import com.mc7.mystoryapp.ui.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setupFullscreen()

        authViewModel.getUserToken().observe(this){ token ->
            if (token != "nothing"){
                GlobalScope.launch {
                    delay(2000)
                    val intentToMainActivity =
                        Intent(this@SplashScreenActivity, MainActivity::class.java)
                    intentToMainActivity.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intentToMainActivity)
                }
            }else{
                GlobalScope.launch {
                    delay(2000)
                    val intentToWelcomeActivity =
                        Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
                    intentToWelcomeActivity.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intentToWelcomeActivity)
                }
            }
        }
    }

    private fun setupFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        supportActionBar?.hide()
    }
}