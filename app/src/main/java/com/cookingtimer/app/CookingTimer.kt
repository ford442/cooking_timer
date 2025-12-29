package com.cookingtimer.app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CookingTimer(
    val id: String,
    var name: String,
    var durationMillis: Long,
    var remainingMillis: Long,
    var notes: String,
    var imagePath: String?,
    var displayMode: DisplayMode,
    var isRunning: Boolean = false,
    var isPaused: Boolean = false
) : Parcelable

enum class DisplayMode {
    DIGITAL,
    ANALOG
}
