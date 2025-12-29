# Cooking Timer

An Android timer app for cooking tasks with the following features:

## Features

- **Multiple Timers**: Create and run multiple cooking timers simultaneously
- **Custom Photos**: Take a photo or upload an image for each timer
- **Notes**: Add cooking notes or instructions to each timer
- **Display Modes**: Toggle between digital and analog clock displays
- **Independent Countdowns**: Each timer counts down independently
- **Notifications**: Get notified when a timer completes

## Requirements

- Android SDK API 24+ (Android 7.0+)
- Gradle 8.0+
- JDK 11 or higher
- Internet connection for initial build (to download dependencies)

## Building

To build the APK:

```bash
./gradlew assembleDebug
```

The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

**Note**: The first build requires internet access to download Android Gradle Plugin and other dependencies from Google's Maven repository.

## Installation

Install the APK on your Android device (API 24+):

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Usage

1. Tap the + button to add a new timer
2. Set a name, duration, and optional photo and notes
3. Choose between digital or analog display
4. Tap the timer to view and control it
5. Start, pause, or reset the timer as needed
6. Long press a timer on the main screen to delete it

## Project Structure

- `app/src/main/java/com/cookingtimer/app/` - Kotlin source files
  - `MainActivity.kt` - Main screen showing all timers
  - `AddTimerActivity.kt` - Screen to create/edit timers
  - `TimerDisplayActivity.kt` - Screen showing timer countdown
  - `TimerManager.kt` - Manages multiple timer instances
  - `CookingTimer.kt` - Data model for timers
  - `AnalogClockView.kt` - Custom view for analog clock display
  - `TimerAdapter.kt` - RecyclerView adapter for timer list
- `app/src/main/res/` - Android resources (layouts, strings, etc.)

## Features Implemented

✅ Multiple independent timers  
✅ Camera capture for timer photos  
✅ Image upload from gallery  
✅ Custom duration setting (hours, minutes, seconds)  
✅ Notes/instructions per timer  
✅ Digital display mode  
✅ Analog clock display mode  
✅ Start/pause/reset controls  
✅ Completion notifications  
✅ Persistent timer state during runtime  

## Permissions

The app requests the following permissions:
- `CAMERA` - For taking photos of cooking tasks
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` - For uploading photos from gallery

