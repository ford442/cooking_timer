# Cooking Timer

An Android timer app for cooking tasks with the following features:

## Features

- **Multiple Timers**: Create and run multiple cooking timers simultaneously
- **Custom Photos**: Take a photo or upload an image for each timer
- **Notes**: Add cooking notes or instructions to each timer
- **Display Modes**: Toggle between digital and analog clock displays
- **Independent Countdowns**: Each timer counts down independently
- **Notifications**: Get notified when a timer completes

## Building

To build the APK:

```bash
./gradlew assembleDebug
```

The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

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

