# NotificationContainer

A powerful Android application that captures, stores, and manages system notifications. This app provides a centralized interface for viewing and managing all your device notifications with a clean and intuitive user experience.

## Features

- **Notification Capture**: Automatically captures all system notifications
- **Notification History**: Maintains a history of all notifications with timestamps
- **Swipe to Delete**: Easy notification management with swipe gestures
- **Notification Details**: Shows detailed information including:
  - Title
  - Description
  - Timestamp
  - Source application
  - Action buttons (when available)
- **Settings Management**: Customizable notification preferences
- **Clean UI**: Material Design interface with smooth animations

## Technical Details

- Built with Java for Android
- Uses Android's NotificationListenerService
- Implements RecyclerView for efficient list management
- Utilizes Realm database for local storage
- Supports Android 4.3 (API level 18) and above

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Build and run the application
4. Grant Notification Listener permission when prompted

## Permissions Required

- Notification Listener Service permission (required for capturing notifications)

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.