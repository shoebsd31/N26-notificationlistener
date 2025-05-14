# N26 Notification Reader

**⚠️ IMPORTANT WARNING ⚠️**

**This application is for experimental purposes only and should NOT be published on the Google Play Store. It is intended to be run locally at your own risk. The developers take no responsibility for any misuse or consequences of using this application.**

This Android application captures push notifications from the N26 banking app and forwards them to a specified email address. It's useful for keeping track of your N26 banking activities through email notifications.

## Project Structure

### Main Components

1. **MainActivity.java**
   - The main UI of the application
   - Handles email configuration and saving
   - Displays notification logs with timestamps
   - Automatically cleans up logs older than 7 days
   - Location: `app/src/main/java/com/example/readn26notifications/MainActivity.java`

2. **N26NotificationListener.java**
   - Background service that listens for N26 notifications
   - Runs as a foreground service with persistent notification
   - Processes and forwards notifications to email
   - Location: `app/src/main/java/com/example/readn26notifications/N26NotificationListener.java`

### Resource Files

1. **activity_main.xml**
   - Main UI layout
   - Contains email input field, logs display, and clear logs button
   - Location: `app/src/main/res/layout/activity_main.xml`

2. **AndroidManifest.xml**
   - App configuration and permissions
   - Location: `app/src/main/AndroidManifest.xml`

## Email Configuration

The app uses two email addresses:

1. **Sender Email (Gmail Account)**
   - This is the Gmail account that will be used to send the notifications
   - Configured in `N26NotificationListener.java`:
     - Email: `shoebsd31@gmail.com`
     - Email Password: `<email password>`
   - To get an App Password:
     1. Go to your Google Account settings
     2. Enable 2-Step Verification if not already enabled
     3. Go to Security → App Passwords
     4. Generate a new app password for "Mail"
     5. Use this password in the code

2. **Recipient Email**
   - Fixed to: `unpickleball@gmail.com`
   - All N26 notifications will be forwarded to this email

### Required Permissions

The app requires the following permissions:
- `INTERNET` - For sending emails
- `POST_NOTIFICATIONS` - For displaying notifications
- `FOREGROUND_SERVICE` - For running the service in the background
- `FOREGROUND_SERVICE_SPECIAL_USE` - For the foreground service type
- `WAKE_LOCK` - To keep the service running
- `BIND_NOTIFICATION_LISTENER_SERVICE` - For listening to notifications (system permission)

## Building and Running

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11 or higher
- Android SDK 34 or higher
- Gradle 8.2 or higher

### Build Steps

1. Clone the repository
2. Open the project in Android Studio
3. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```
4. The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

### Installation

1. Enable "Install from Unknown Sources" on your Android device
2. Transfer the APK to your device
3. Install the APK
4. Grant the required permissions when prompted:
   - Notification access
   - Internet access
   - Background service permissions

## Usage

1. After installation, open the app
2. The service will start automatically in the background
3. A persistent notification will appear to indicate the service is running
4. All N26 notifications will be automatically forwarded to unpickleball@gmail.com
5. The app's main screen shows a log of all notifications with timestamps
6. Logs older than 7 days are automatically cleaned up
7. Use the "Clear Logs" button to manually clear all logs

## Troubleshooting

### Common Issues

1. **Notifications not being forwarded**
   - Ensure notification access is granted
   - Check if N26 app is installed and notifications are enabled
   - Verify the service is running (check for persistent notification)

2. **Email not received**
   - Check spam folder
   - Verify sender Gmail account is properly configured
   - Ensure device has internet connection
   - Check if App Password is correct

3. **Service stops running**
   - Check if the device has battery optimization enabled
   - Ensure the app is not being killed by the system
   - Verify all permissions are granted

## Dependencies

The app uses the following main dependencies:
- androidx.appcompat:appcompat:1.6.1
- com.google.android.material:material:1.11.0
- androidx.constraintlayout:constraintlayout:2.1.4
- com.sun.mail:android-mail:1.6.7
- com.sun.mail:android-activation:1.6.7

## Security Notes

- The app only reads notifications from the N26 app
- Email credentials are stored in the code (not recommended for production)
- All data is stored locally on the device
- No data is sent to any third-party servers except for the email notifications
- The sender Gmail account requires an App Password for security
- Never commit your actual Gmail credentials to version control

## Contributing

Feel free to submit issues and enhancement requests!