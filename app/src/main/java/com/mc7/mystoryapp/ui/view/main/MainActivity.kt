package com.mc7.mystoryapp.ui.view.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mc7.mystoryapp.R
import com.mc7.mystoryapp.databinding.ActivityMainBinding
import com.mc7.mystoryapp.ui.view.addstory.AddStoryActivity
import com.mc7.mystoryapp.ui.view.WelcomeActivity
import com.mc7.mystoryapp.ui.view.login.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "List Story"

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        val listAdapter = ListStoryAdapter()
        binding.rvStory.adapter = listAdapter

        viewModel.isFillStories.observe(this) { items ->
            listAdapter.submitList(items)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isError.observe(this) {
            showError(it)
        }

        binding.fabAddStory.setOnClickListener {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity
            ).toBundle()

            val intentToAddStoryActivity =
                Intent(this@MainActivity, AddStoryActivity::class.java)

            startActivity(intentToAddStoryActivity, optionsCompat)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: String?) {
        binding.tvErrorMessage.visibility = if (isError != null) View.VISIBLE else View.GONE

        binding.tvErrorMessage.text = isError
    }

    private fun showAlert(message: String) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)

        alert.setTitle(message)

        alert.setPositiveButton("Logout") { _, _ ->
            authViewModel.saveUserToken("nothing")

            val intentToWelcomeActivity =
                Intent(this@MainActivity, WelcomeActivity::class.java)
            intentToWelcomeActivity.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentToWelcomeActivity.putExtra("extra_message", "Berhasil Logout!")

            startActivity(intentToWelcomeActivity)
        }

        alert.setNegativeButton("Cancel") { _, _ -> }

        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_logout -> {
                showAlert("Apakah anda yakin ingin logout?")
            }
        }

        return super.onOptionsItemSelected(item)
    }
}