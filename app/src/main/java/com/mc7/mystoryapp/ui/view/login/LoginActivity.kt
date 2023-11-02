package com.mc7.mystoryapp.ui.view.login

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.mc7.mystoryapp.R
import com.mc7.mystoryapp.data.remote.response.LoginResponse
import com.mc7.mystoryapp.databinding.ActivityLoginBinding
import com.mc7.mystoryapp.ui.view.main.MainActivity
import com.mc7.mystoryapp.ui.view.register.RegisterActivity
import com.mc7.mystoryapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            viewModel.isLoading.observe(this@LoginActivity) {
                showLoading(it)
            }

            viewModel.isSuccess.observe(this@LoginActivity) {
                showAlert(it, true)
            }

            viewModel.isError.observe(this@LoginActivity) {
                showAlert(it, false)
            }

            btnLogin.setOnClickListener {
                val email = edtEmailLogin.text.toString()
                val password = edtPassLogin.text.toString()

                loginUser(email, password)
            }
        }

        playAnimation()
        setDaftarBtn()
    }

    private fun loginProses(data: LoginResponse) {
        if (data.error == true) {
            viewModel.isError(data.message)
            Toast.makeText(this@LoginActivity, data.message, Toast.LENGTH_LONG).show()
        } else {
            authViewModel.saveUserToken(data.loginResult?.token.toString())

            viewModel.isSuccess("Berhasil")
        }
    }

    private fun loginUser(email: String, password: String) {
        viewModel.loginUser(email, password)
            .observe(this@LoginActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        viewModel.isLoading(true)
                    }

                    is Result.Success -> {
                        viewModel.isLoading(false)
                        viewModel.isError(null)
                        loginProses(result.data)
                    }

                    is Result.Error -> {
                        viewModel.isLoading(false)
                        viewModel.isSuccess(null)
                        viewModel.isError("Gagal mendaftar! Pastikan untuk mengisi data anda dengan benar!")
                    }
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showAlert(message: String?, type: Boolean) {
        if (message != null) {
            val alert: AlertDialog.Builder = AlertDialog.Builder(this)

            if (type) {
                alert.setTitle(message)

                alert.setPositiveButton("Ok") { _, _ ->
                    val intentToMainActivity =
                        Intent(this@LoginActivity, MainActivity::class.java)
                    intentToMainActivity.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intentToMainActivity)
                }
            } else {
                alert.setTitle(message)

                // no action
                alert.setPositiveButton("Ok") { _, _ -> }
            }

            alert.show()
        }
    }

    private fun setDaftarBtn(){
        val ss = SpannableString(getString(R.string.txt_daftar, "Register Now!"))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@LoginActivity,
                    Pair(binding.btnLogin, "btnLogin"),
                    Pair(binding.txtWelcome, "tvWelcome"),
                    Pair(binding.txtTitleLogin, "tvTitle"),
                    Pair(binding.txtEmail, "tvEmail"),
                    Pair(binding.edtEmailLogin, "edtEmail"),
                    Pair(binding.txtPass, "tvPass"),
                    Pair(binding.edtPassLogin, "edtPass"),
                    Pair(binding.txtDaftar, "tvDaftar"),
                )

                val intentToRegisterActivity =
                    Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intentToRegisterActivity, optionsCompat.toBundle())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.links)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 21, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.txtDaftar.text = ss
        binding.txtDaftar.movementMethod = LinkMovementMethod.getInstance()
        binding.txtDaftar.highlightColor = Color.TRANSPARENT
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        val movingImg: Long = 6000

        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = movingImg
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}