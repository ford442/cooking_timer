package com.cookingtimer.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTimerActivity : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var notesInput: TextInputEditText
    private lateinit var imagePreview: ImageView
    private lateinit var hoursPicker: NumberPicker
    private lateinit var minutesPicker: NumberPicker
    private lateinit var secondsPicker: NumberPicker
    private lateinit var radioDigital: RadioButton
    private lateinit var radioAnalog: RadioButton

    private var currentPhotoPath: String? = null
    private var existingTimer: CookingTimer? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                imagePreview.setImageBitmap(bitmap)
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imagePreview.setImageURI(it)
            currentPhotoPath = copyImageToInternal(it)
        }
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            takePhoto()
        } else {
            Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
        }
    }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pickImage()
        } else {
            Toast.makeText(this, R.string.storage_permission_required, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_timer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nameInput = findViewById(R.id.timerNameInput)
        notesInput = findViewById(R.id.notesInput)
        imagePreview = findViewById(R.id.timerImagePreview)
        hoursPicker = findViewById(R.id.hoursPicker)
        minutesPicker = findViewById(R.id.minutesPicker)
        secondsPicker = findViewById(R.id.secondsPicker)
        radioDigital = findViewById(R.id.radioDigital)
        radioAnalog = findViewById(R.id.radioAnalog)

        setupNumberPickers()

        val timerId = intent.getStringExtra("TIMER_ID")
        if (timerId != null) {
            existingTimer = TimerManager.getTimer(timerId)
            populateExistingTimer()
        }

        findViewById<Button>(R.id.btnTakePhoto).setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        findViewById<Button>(R.id.btnChoosePhoto).setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    pickImage()
                } else {
                    requestStoragePermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pickImage()
                } else {
                    requestStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveTimer()
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }

    private fun setupNumberPickers() {
        hoursPicker.minValue = 0
        hoursPicker.maxValue = 23
        hoursPicker.value = 0

        minutesPicker.minValue = 0
        minutesPicker.maxValue = 59
        minutesPicker.value = 1

        secondsPicker.minValue = 0
        secondsPicker.maxValue = 59
        secondsPicker.value = 0
    }

    private fun populateExistingTimer() {
        existingTimer?.let { timer ->
            nameInput.setText(timer.name)
            notesInput.setText(timer.notes)
            
            val totalSeconds = timer.durationMillis / 1000
            hoursPicker.value = (totalSeconds / 3600).toInt()
            minutesPicker.value = ((totalSeconds % 3600) / 60).toInt()
            secondsPicker.value = (totalSeconds % 60).toInt()

            when (timer.displayMode) {
                DisplayMode.DIGITAL -> radioDigital.isChecked = true
                DisplayMode.ANALOG -> radioAnalog.isChecked = true
            }

            timer.imagePath?.let { path ->
                currentPhotoPath = path
                val bitmap = BitmapFactory.decodeFile(path)
                imagePreview.setImageBitmap(bitmap)
            }
        }
    }

    private fun takePhoto() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        photoFile?.let {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                it
            )
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "TIMER_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun copyImageToInternal(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "TIMER_${timeStamp}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private fun saveTimer() {
        val name = nameInput.text?.toString()?.trim() ?: ""
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a timer name", Toast.LENGTH_SHORT).show()
            return
        }

        val hours = hoursPicker.value
        val minutes = minutesPicker.value
        val seconds = secondsPicker.value
        val totalMillis = ((hours * 3600L) + (minutes * 60L) + seconds) * 1000L

        if (totalMillis == 0L) {
            Toast.makeText(this, "Please set a duration", Toast.LENGTH_SHORT).show()
            return
        }

        val notes = notesInput.text?.toString()?.trim() ?: ""
        val displayMode = if (radioDigital.isChecked) DisplayMode.DIGITAL else DisplayMode.ANALOG

        if (existingTimer != null) {
            val updatedTimer = existingTimer!!.copy(
                name = name,
                durationMillis = totalMillis,
                remainingMillis = totalMillis,
                notes = notes,
                imagePath = currentPhotoPath,
                displayMode = displayMode
            )
            TimerManager.updateTimer(existingTimer!!.id, updatedTimer)
        } else {
            val newTimer = TimerManager.createNewTimer().copy(
                name = name,
                durationMillis = totalMillis,
                remainingMillis = totalMillis,
                notes = notes,
                imagePath = currentPhotoPath,
                displayMode = displayMode
            )
            TimerManager.addTimer(newTimer)
        }

        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
