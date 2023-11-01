package com.mc7.mystoryapp.ui.view.register

import android.animation.ObjectAnimator
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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.mc7.mystoryapp.R
import com.mc7.mystoryapp.databinding.ActivityRegisterBinding
import com.mc7.mystoryapp.ui.view.login.LoginActivity
import com.mc7.mystoryapp.ui.view.WelcomeActivity
import com.mc7.mystoryapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {


            viewModel.isLoading.observe(this@RegisterActivity) {
                showLoading(it)
            }

            viewModel.isSuccess.observe(this@RegisterActivity) {
                showAlert(it, true)
            }

            viewModel.isError.observe(this@RegisterActivity) {
                showAlert(it, false)
            }

            btnRegister.setOnClickListener {
                val name = edtName.text.toString()
                val email = edtEmail.text.toString()
                val password = edtPass.text.toString()

                registerUser(name, email, password)
            }
        }

        playAnimation()
        setLoginBtn()
    }

    private fun registerUser(name: String, email: String, password: String) {
        viewModel.registerUser(name, email, password)
            .observe(this@RegisterActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        viewModel.isLoading(true)
                        viewModel.isError(null)
                        viewModel.isSuccess(null)
                    }

                    is Result.Success -> {
                        viewModel.isLoading(false)
                        viewModel.isError(null)

                        viewModel.isSuccess(result.data.message)
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
                    val intentToWelcomeActivity =
                        Intent(this@RegisterActivity, WelcomeActivity::class.java)
                    intentToWelcomeActivity.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intentToWelcomeActivity)
                }
            } else {
                alert.setTitle(message)

                // no action
                alert.setPositiveButton("Ok") { _, _ -> }
            }

            alert.show()
        }
    }

    private fun setLoginBtn() {
        val ss = SpannableString(getString(R.string.txt_login_ya, "Login Now!"))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@RegisterActivity,
                    Pair(binding.btnRegister, "btnRegister"),
                    Pair(binding.txtTitleRegister, "tvTitle"),
                    Pair(binding.txtName, "tvName"),
                    Pair(binding.edtName, "edtName"),
                    Pair(binding.txtEmail, "tvEmail"),
                    Pair(binding.edtEmail, "edtEmail"),
                    Pair(binding.txtPass, "tvPass"),
                    Pair(binding.edtPass, "edtPass"),
                    Pair(binding.txtLogin, "tvLogin"),
                )

                val intentToLoginActivity = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intentToLoginActivity, optionsCompat.toBundle())
            }


            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.links)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 15, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.txtLogin.text = ss
        binding.txtLogin.movementMethod = LinkMovementMethod.getInstance()
        binding.txtLogin.highlightColor = Color.TRANSPARENT
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}