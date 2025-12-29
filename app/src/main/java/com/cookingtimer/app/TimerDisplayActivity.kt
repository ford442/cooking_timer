package com.cookingtimer.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import android.graphics.BitmapFactory
import java.io.File

class TimerDisplayActivity : AppCompatActivity(), TimerManager.TimerUpdateListener {

    private lateinit var timerNameText: TextView
    private lateinit var timerNotesText: TextView
    private lateinit var timerImage: ImageView
    private lateinit var digitalDisplay: TextView
    private lateinit var analogDisplay: AnalogClockView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button
    private lateinit var btnToggleDisplay: Button

    private var timerId: String? = null
    private var currentTimer: CookingTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_display)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        timerNameText = findViewById(R.id.timerName)
        timerNotesText = findViewById(R.id.timerNotes)
        timerImage = findViewById(R.id.timerImage)
        digitalDisplay = findViewById(R.id.digitalDisplay)
        analogDisplay = findViewById(R.id.analogDisplay)
        btnStart = findViewById(R.id.btnStart)
        btnPause = findViewById(R.id.btnPause)
        btnReset = findViewById(R.id.btnReset)
        btnToggleDisplay = findViewById(R.id.btnToggleDisplay)

        timerId = intent.getStringExtra("TIMER_ID")

        TimerManager.addListener(this)

        btnStart.setOnClickListener {
            timerId?.let { id ->
                TimerManager.startTimer(id)
            }
        }

        btnPause.setOnClickListener {
            timerId?.let { id ->
                TimerManager.pauseTimer(id)
            }
        }

        btnReset.setOnClickListener {
            timerId?.let { id ->
                TimerManager.resetTimer(id)
            }
        }

        btnToggleDisplay.setOnClickListener {
            currentTimer?.let { timer ->
                val newMode = if (timer.displayMode == DisplayMode.DIGITAL) {
                    DisplayMode.ANALOG
                } else {
                    DisplayMode.DIGITAL
                }
                val updatedTimer = timer.copy(displayMode = newMode)
                TimerManager.updateTimer(timer.id, updatedTimer)
            }
        }

        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        TimerManager.removeListener(this)
    }

    override fun onTimerUpdated(timer: CookingTimer) {
        if (timer.id == timerId) {
            runOnUiThread {
                updateUI()
            }
        }
    }

    override fun onTimerCompleted(timer: CookingTimer) {
        if (timer.id == timerId) {
            runOnUiThread {
                updateUI()
                showCompletionNotification()
            }
        }
    }

    private fun updateUI() {
        timerId?.let { id ->
            currentTimer = TimerManager.getTimer(id)
            currentTimer?.let { timer ->
                timerNameText.text = timer.name
                timerNotesText.text = timer.notes
                timerNotesText.visibility = if (timer.notes.isNotEmpty()) View.VISIBLE else View.GONE

                timer.imagePath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(path)
                        timerImage.setImageBitmap(bitmap)
                        timerImage.visibility = View.VISIBLE
                    } else {
                        timerImage.visibility = View.GONE
                    }
                } ?: run {
                    timerImage.visibility = View.GONE
                }

                when (timer.displayMode) {
                    DisplayMode.DIGITAL -> {
                        digitalDisplay.visibility = View.VISIBLE
                        analogDisplay.visibility = View.GONE
                        digitalDisplay.text = formatTime(timer.remainingMillis)
                    }
                    DisplayMode.ANALOG -> {
                        digitalDisplay.visibility = View.GONE
                        analogDisplay.visibility = View.VISIBLE
                        analogDisplay.setTime(timer.durationMillis, timer.remainingMillis)
                    }
                }

                btnStart.isEnabled = !timer.isRunning
                btnPause.isEnabled = timer.isRunning
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun showCompletionNotification() {
        val channelId = "timer_complete"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer Completions",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.timer_complete))
            .setContentText(currentTimer?.name ?: "Timer")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(timerId.hashCode(), notification)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
