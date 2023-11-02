package com.mc7.mystoryapp.ui.view.addstory

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mc7.mystoryapp.R
import com.mc7.mystoryapp.databinding.ActivityAddStoryBinding
import com.mc7.mystoryapp.ui.view.main.MainActivity
import com.mc7.mystoryapp.utils.Result
import com.mc7.mystoryapp.utils.reduceFileImage
import com.mc7.mystoryapp.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Float? = null
    private var lon: Float? = null
    private val viewModel: AddStoryViewModel by viewModels()

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraXActivity.CAMERAX_RESULT) {
            currentImageUri =
                it.data?.getStringExtra(
                    CameraXActivity.EXTRA_CAMERAX_IMAGE
                )?.toUri()

            showImage()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Add Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.topAppBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.withMyLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isSuccess.observe(this) {
            showAlert(it, true)
        }

        viewModel.isError.observe(this) {
            showAlert(it, false)
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        lat = location.latitude.toFloat()
                        lon = location.longitude.toFloat()

                        Log.d("UploadStory", "$lat + $lon")
                    } else {
                        showAlert("Location is not found. Try Again", false)
                    }
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this@AddStoryActivity).reduceFileImage()

            Log.d("Image File", "showImage: ${imageFile.path}")

            val description = binding.edtDescription.text.toString()

            val requestBody =
                description.toRequestBody("text/plain".toMediaType())
            val requestImageFile =
                imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody =
                MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

            viewModel.uploadStory(multipartBody, requestBody, lat, lon)
                .observe(this@AddStoryActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            viewModel.isLoading(true)
                        }

                        is Result.Success -> {
                            viewModel.isLoading(false)
                            viewModel.isError(null)
                            viewModel.isSuccess(result.data.message)
                        }

                        is Result.Error -> {
                            viewModel.isLoading(false)
                            viewModel.isError("Gagal mengunggah! " + result.error)
                        }
                    }
                }
        } ?: showToast()
    }

    private fun showToast() {
        Toast.makeText(this@AddStoryActivity, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT)
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showAlert(message: String?, type: Boolean) {
        if (message != null) {
            val alert: AlertDialog.Builder = AlertDialog.Builder(this)

            if (type) {
                alert.setTitle(message)

                alert.setPositiveButton("Ok") { _, _ ->
                    val intentToMainActivity =
                        Intent(this@AddStoryActivity, MainActivity::class.java)
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

    private fun startCamera() {
        val intent = Intent(this@AddStoryActivity, CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImgStory.setImageURI(it)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}