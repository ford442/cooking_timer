package com.cookingtimer.app

import android.os.CountDownTimer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object TimerManager {
    private val timers = mutableListOf<CookingTimer>()
    private val activeCountdowns = ConcurrentHashMap<String, CountDownTimer>()
    private val listeners = mutableListOf<TimerUpdateListener>()

    interface TimerUpdateListener {
        fun onTimerUpdated(timer: CookingTimer)
        fun onTimerCompleted(timer: CookingTimer)
    }

    fun addListener(listener: TimerUpdateListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: TimerUpdateListener) {
        listeners.remove(listener)
    }

    fun addTimer(timer: CookingTimer) {
        timers.add(timer)
        notifyTimerUpdated(timer)
    }

    fun removeTimer(timerId: String) {
        stopTimer(timerId)
        timers.removeAll { it.id == timerId }
    }

    fun getTimer(timerId: String): CookingTimer? {
        return timers.find { it.id == timerId }
    }

    fun getAllTimers(): List<CookingTimer> {
        return timers.toList()
    }

    fun startTimer(timerId: String) {
        val timer = getTimer(timerId) ?: return
        
        if (timer.remainingMillis <= 0) {
            timer.remainingMillis = timer.durationMillis
        }

        stopTimer(timerId)

        val countdown = object : CountDownTimer(timer.remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer.remainingMillis = millisUntilFinished
                timer.isRunning = true
                timer.isPaused = false
                notifyTimerUpdated(timer)
            }

            override fun onFinish() {
                timer.remainingMillis = 0
                timer.isRunning = false
                timer.isPaused = false
                notifyTimerCompleted(timer)
            }
        }

        activeCountdowns[timerId] = countdown
        countdown.start()
    }

    fun pauseTimer(timerId: String) {
        val timer = getTimer(timerId) ?: return
        stopTimer(timerId)
        timer.isRunning = false
        timer.isPaused = true
        notifyTimerUpdated(timer)
    }

    fun resetTimer(timerId: String) {
        val timer = getTimer(timerId) ?: return
        stopTimer(timerId)
        timer.remainingMillis = timer.durationMillis
        timer.isRunning = false
        timer.isPaused = false
        notifyTimerUpdated(timer)
    }

    fun stopTimer(timerId: String) {
        activeCountdowns[timerId]?.cancel()
        activeCountdowns.remove(timerId)
    }

    fun updateTimer(timerId: String, updatedTimer: CookingTimer) {
        val index = timers.indexOfFirst { it.id == timerId }
        if (index != -1) {
            val wasRunning = timers[index].isRunning
            timers[index] = updatedTimer
            if (wasRunning) {
                startTimer(timerId)
            }
            notifyTimerUpdated(updatedTimer)
        }
    }

    private fun notifyTimerUpdated(timer: CookingTimer) {
        listeners.forEach { it.onTimerUpdated(timer) }
    }

    private fun notifyTimerCompleted(timer: CookingTimer) {
        listeners.forEach { it.onTimerCompleted(timer) }
    }

    fun createNewTimer(): CookingTimer {
        return CookingTimer(
            id = UUID.randomUUID().toString(),
            name = "New Timer",
            durationMillis = 60000,
            remainingMillis = 60000,
            notes = "",
            imagePath = null,
            displayMode = DisplayMode.DIGITAL
        )
    }
}
