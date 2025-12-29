# Build Instructions for Cooking Timer Android App

## Overview
This is a complete Android application for managing multiple cooking timers with photos, notes, and multiple display modes.

## Prerequisites

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   java -version
   ```

2. **Android SDK** (API 24 or higher)
   - Can be installed via Android Studio or command-line tools
   - Set ANDROID_HOME environment variable

3. **Internet Connection** (for first-time build only)
   - Required to download Android Gradle Plugin (AGP) and dependencies from Google's Maven repository

## Build Steps

### Option 1: Using Gradle Wrapper (Recommended)

```bash
# Make gradlew executable (Unix/Linux/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease
```

### Option 2: Using Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the project directory
4. Wait for Gradle sync to complete
5. Build > Build Bundle(s) / APK(s) > Build APK(s)

## Output Locations

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`

## Installation

### Via ADB (Android Debug Bridge)

```bash
# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Or install and replace existing
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Via File Transfer

1. Copy the APK file to your Android device
2. Enable "Install from Unknown Sources" in device settings
3. Open the APK file on the device to install

## Troubleshooting

### Build Fails with "Plugin not found"

This happens when internet access is restricted or Google's Maven repository is not accessible.

**Solution**: Ensure you have internet access and that the following repositories are accessible:
- https://dl.google.com/dl/android/maven2/
- https://repo1.maven.org/maven2/

### Gradle Daemon Issues

```bash
# Stop all Gradle daemons
./gradlew --stop

# Clear Gradle cache
rm -rf ~/.gradle/caches/
```

### SDK Location Issues

Create or update `local.properties` file in the project root:
```
sdk.dir=/path/to/android/sdk
```

### Memory Issues

Add to `gradle.properties`:
```
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError
```

## Verifying the Build

After building, you can verify the APK:

```bash
# List APK contents
unzip -l app/build/outputs/apk/debug/app-debug.apk

# Get APK info
aapt dump badging app/build/outputs/apk/debug/app-debug.apk
```

## Clean Build

If you encounter issues, try a clean build:

```bash
./gradlew clean
./gradlew assembleDebug
```

## Running Tests (when available)

```bash
# Run unit tests
./gradlew test

# Run Android instrumentation tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```

## Project Structure

The project follows standard Android application structure:
- `app/build.gradle.kts` - App-level Gradle configuration
- `build.gradle.kts` - Project-level Gradle configuration
- `settings.gradle.kts` - Gradle settings
- `app/src/main/` - Main application source
  - `java/` - Kotlin source files
  - `res/` - Android resources
  - `AndroidManifest.xml` - App manifest

## Dependencies

The app uses the following major dependencies:
- AndroidX Core KTX
- AndroidX AppCompat
- Material Components
- ConstraintLayout
- Lifecycle components
- RecyclerView

All dependencies are managed in `app/build.gradle.kts` and will be automatically downloaded during the first build.

## Current Limitations

- **Internet Required**: First build requires internet access to download AGP and dependencies
- **No Pre-built APK**: APK must be built from source
- **No CI/CD**: No automated build pipeline configured yet

## Next Steps

After successful build:
1. Install the APK on an Android device (API 24+)
2. Grant camera and storage permissions when prompted
3. Start creating cooking timers with photos and notes!

## Support

For build issues:
1. Check that all prerequisites are met
2. Ensure internet connectivity for first build
3. Verify Android SDK is properly installed
4. Check Gradle and JDK versions compatibility
