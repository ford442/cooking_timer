# Cooking Timer App - Features Documentation

## Overview
A comprehensive Android cooking timer application that allows users to manage multiple timers with custom photos, notes, and flexible display options.

## Core Features

### 1. Multiple Independent Timers
- Create unlimited cooking timers
- Each timer runs independently with its own countdown
- View all timers in a scrollable list on the main screen
- Timers continue running even when viewing other timers

**Implementation**: `TimerManager.kt` manages multiple timer instances using a concurrent hash map for active countdowns.

### 2. Custom Timer Photos

#### Camera Capture
- Take photos directly from the app
- Uses device camera with proper permission handling
- Photos are saved to app's external files directory
- Automatic file naming with timestamps

#### Image Upload
- Select existing photos from device gallery
- Supports image picker for Android 13+ (READ_MEDIA_IMAGES)
- Backwards compatible with READ_EXTERNAL_STORAGE for older versions
- Images are copied to app storage for consistency

**Implementation**: `AddTimerActivity.kt` uses ActivityResultContracts for camera and image selection.

### 3. Timer Configuration

#### Duration Setting
- Set hours (0-23)
- Set minutes (0-59)
- Set seconds (0-59)
- Uses Android NumberPicker widgets for intuitive selection
- Minimum duration: 1 second
- Maximum duration: 23:59:59

#### Timer Information
- Custom timer name (required)
- Optional cooking notes/instructions
- Multi-line text support for detailed notes

**Implementation**: `activity_add_timer.xml` layout with NumberPickers and TextInputEditText.

### 4. Display Modes

#### Digital Display
- Large, easy-to-read numeric display (HH:MM:SS format)
- Shows remaining time in hours, minutes, and seconds
- Color-coded display (primary color)
- Format: 00:00:00

#### Analog Clock Display
- Custom-drawn clock face with hour markers
- Animated clock hand showing progress
- Visual progress arc
- Circular time representation
- Smooth updates (10 updates per second)

**Implementation**: `AnalogClockView.kt` custom View with Canvas drawing.

### 5. Timer Controls

#### Start/Pause/Reset
- **Start**: Begin countdown from current remaining time
- **Pause**: Stop countdown and preserve remaining time
- **Reset**: Restore timer to original duration

#### Display Toggle
- Switch between digital and analog display modes
- Instant mode switching without interrupting countdown
- Mode preference saved per timer

**Implementation**: `TimerDisplayActivity.kt` handles all control logic.

### 6. Timer List Management

#### Main Screen Features
- Displays all created timers in a scrollable list
- Each timer card shows:
  - Timer photo (if available)
  - Timer name
  - Current remaining time (HH:MM:SS)
  - Notes preview (up to 2 lines)
  - Status indicator (Running/Paused/Completed)

#### Status Indicators
- **Running**: Green text with "Running" badge
- **Paused**: Orange text with "Paused" badge
- **Completed**: Red text with "Completed" badge

#### Interactions
- **Tap**: Open timer in full-screen display mode
- **Long Press**: Delete timer (immediate removal)

**Implementation**: `MainActivity.kt` with RecyclerView and `TimerAdapter.kt`.

### 7. Notifications

#### Completion Alerts
- System notification when timer completes
- Shows timer name in notification
- Plays default notification sound
- Notification channel for Android O+
- Auto-dismiss after user interaction

**Implementation**: `TimerDisplayActivity.kt` creates notifications using NotificationCompat.

### 8. Real-Time Updates

#### Live Countdown
- Updates every 100ms for smooth countdown
- All timer displays update simultaneously
- Main screen shows live countdown for all timers
- Timer display screen updates in real-time

#### State Synchronization
- Timer state synced across all screens
- Observer pattern for update notifications
- UI updates on main thread automatically

**Implementation**: `TimerManager.TimerUpdateListener` interface with callbacks.

## User Interface

### Material Design
- Follows Material Design guidelines
- Material Components library
- Consistent color scheme
- Elevation and shadows for depth
- Floating Action Button for primary action

### Layouts

#### MainActivity
- Floating Action Button (+) for adding timers
- RecyclerView for timer list
- Empty state message when no timers exist

#### AddTimerActivity
- ScrollView for all content
- Image preview area
- Photo action buttons (Take Photo / Choose Photo)
- NumberPickers for time selection
- Radio buttons for display mode
- Text fields for name and notes
- Save/Cancel buttons

#### TimerDisplayActivity
- Full-screen timer photo (if available)
- Timer name at top
- Notes below name
- Large timer display (digital or analog)
- Control buttons (Start/Pause/Reset)
- Display mode toggle button

### Responsive Design
- Adapts to different screen sizes
- ScrollView for overflow content
- Proper margins and padding
- Touch-friendly button sizes

## Data Management

### Timer Data Model
```kotlin
data class CookingTimer(
    val id: String,              // UUID
    var name: String,            // Timer name
    var durationMillis: Long,    // Original duration
    var remainingMillis: Long,   // Current remaining time
    var notes: String,           // Cooking instructions
    var imagePath: String?,      // Photo file path
    var displayMode: DisplayMode, // DIGITAL or ANALOG
    var isRunning: Boolean,      // Running state
    var isPaused: Boolean        // Paused state
)
```

### State Management
- In-memory timer storage (lost on app restart)
- Active countdown management with Android CountDownTimer
- Thread-safe operations with ConcurrentHashMap
- Parcelable support for passing between activities

### File Storage
- Photos stored in external files directory (Pictures)
- FileProvider for camera capture
- Proper file URI handling for Android 7+
- Automatic cleanup when timer deleted (future enhancement)

## Permissions

### Required Permissions
1. **CAMERA**: Taking photos of cooking tasks
2. **READ_EXTERNAL_STORAGE**: Uploading photos (Android 12 and below)
3. **READ_MEDIA_IMAGES**: Uploading photos (Android 13+)

### Permission Handling
- Runtime permission requests
- User-friendly permission denial messages
- Graceful degradation if permissions denied
- Re-request capability

## Technical Implementation

### Architecture
- Activity-based navigation
- Singleton TimerManager for state
- Observer pattern for updates
- Custom View for analog clock

### Threading
- CountDownTimer runs on background thread
- UI updates posted to main thread
- Thread-safe data structures

### Libraries
- AndroidX Core KTX
- AndroidX AppCompat
- Material Components
- ConstraintLayout
- Lifecycle components
- RecyclerView

### Minimum Requirements
- Android API 24 (Android 7.0)
- Target API 34 (Android 14)
- Kotlin 1.9.0

## Future Enhancements (Not Implemented)

- Persistent storage (save timers to database)
- Timer templates for common cooking tasks
- Sound selection for completion notification
- Vibration patterns
- Widget support
- Dark theme
- Timer history
- Export/import timer configurations
- Voice commands
- Background service for reliable countdown
- Foreground service notification while running
- Lock screen widget

## Testing Recommendations

1. **Multiple Timers**: Create 3-5 timers with different durations
2. **Concurrent Running**: Start multiple timers simultaneously
3. **Photos**: Test both camera and gallery options
4. **Display Modes**: Switch between digital/analog while running
5. **Pause/Resume**: Test pause and resume functionality
6. **Completion**: Let timer run to completion
7. **Navigation**: Switch between screens while timers run
8. **Permissions**: Test with permissions granted and denied
9. **Edge Cases**: 1-second timer, 23:59:59 timer
10. **Device Rotation**: Test landscape orientation

## Known Limitations

1. **No Persistence**: Timers lost when app closes
2. **No Background Service**: Timers may pause if app is killed by system
3. **No Lock Screen Display**: Timer only visible when app is open
4. **Manual Deletion Only**: No swipe-to-delete gesture
5. **No Timer Reordering**: Timers appear in creation order
6. **No Search/Filter**: May be difficult with many timers
7. **No Timer Groups**: Cannot organize timers by meal or recipe
