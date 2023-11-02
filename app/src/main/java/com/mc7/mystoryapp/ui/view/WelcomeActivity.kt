package com.mc7.mystoryapp.ui.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.mc7.mystoryapp.databinding.ActivityWelcomeBinding
import com.mc7.mystoryapp.ui.view.login.LoginActivity
import com.mc7.mystoryapp.ui.view.register.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val message: String? = intent.getStringExtra(EXTRA_MESSAGE)

        if (!message.isNullOrEmpty()){
            showAlert(message)
            intent.removeExtra(EXTRA_MESSAGE)
        }

        playAnimation()

        binding.btnToLogin.setOnClickListener {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                   this@WelcomeActivity
                ).toBundle()

            val intentToLoginActivity = Intent(this, LoginActivity::class.java)
            startActivity(intentToLoginActivity, optionsCompat)
        }

        binding.btnToDaftar.setOnClickListener {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@WelcomeActivity
            ).toBundle()

            val intentToRegisterActivity = Intent(this, RegisterActivity::class.java)
            startActivity(intentToRegisterActivity, optionsCompat)
        }
    }

    private fun showAlert(message: String) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)

        alert.setTitle(message)

        alert.setPositiveButton("Ok") { _, _ ->}

        alert.show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val imgWelcome = ObjectAnimator.ofFloat(binding.imgWelcome, View.ALPHA, 1f).setDuration(300)
        val tvTitle = ObjectAnimator.ofFloat(binding.txtWelcome, View.ALPHA, 1f).setDuration(300)
        val tvDesc = ObjectAnimator.ofFloat(binding.txtDescription, View.ALPHA, 1f).setDuration(300)
        val btnDaftar = ObjectAnimator.ofFloat(binding.btnToDaftar, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnToLogin, View.ALPHA, 1f).setDuration(300)

        val togetherBtn = AnimatorSet().apply {
            playTogether(btnLogin, btnDaftar)
        }

        AnimatorSet().apply {
            playSequentially(imgWelcome, tvTitle, tvDesc, togetherBtn)
            start()
        }
    }

    companion object {
        const val EXTRA_MESSAGE = "extra_message"
    }
}