# N26 Notification Reader

This Android application captures push notifications from the N26 banking app and forwards them to a specified email address. It's useful for keeping track of your N26 banking activities through email notifications.

## Project Structure

### Main Components

1. **MainActivity.java**
   - The main UI of the application
   - Handles email configuration and saving
   - Displays notification logs
   - Location: `app/src/main/java/com/example/readn26notifications/MainActivity.java`

2. **N26NotificationListenerService.java**
   - Background service that listens for N26 notifications
   - Processes and forwards notifications to email
   - Location: `app/src/main/java/com/example/readn26notifications/N26NotificationListenerService.java`

3. **N26NotificationListener.java**
   - Handles the actual notification interception
   - Filters for N26 app notifications
   - Location: `app/src/main/java/com/example/readn26notifications/N26NotificationListener.java`

4. **EmailSender.java**
   - Manages email sending functionality
   - Uses JavaMail API for sending emails
   - Location: `app/src/main/java/com/example/readn26notifications/EmailSender.java`

### Resource Files

1. **activity_main.xml**
   - Main UI layout
   - Contains email input field and logs display
   - Location: `app/src/main/res/layout/activity_main.xml`

2. **AndroidManifest.xml**
   - App configuration and permissions
   - Location: `app/src/main/AndroidManifest.xml`

## Email Configuration

The app uses two email addresses:

1. **Sender Email (Gmail Account)**
   - This is the Gmail account that will be used to send the notifications
   - You need to modify `N26NotificationListener.java` and replace:
     - `"your-email@gmail.com"` with your Gmail address
     - `"your-app-password"` with an App Password from your Google Account
   - To get an App Password:
     1. Go to your Google Account settings
     2. Enable 2-Step Verification if not already enabled
     3. Go to Security → App Passwords
     4. Generate a new app password for "Mail"
     5. Use this password in the code

2. **Recipient Email (Configuration)**
   - This is where you want to receive the N26 notifications
   - Configure this in the app's UI
   - Can be any email address (doesn't have to be Gmail)

### Required Permissions

The app requires the following permissions:
- Notification access (to read N26 notifications)
- Internet access (to send emails)
- Email access (to send notifications)

## Building and Running

### Prerequisites

- Android Studio
- JDK 11 or higher
- Android SDK 31 or higher
- Gradle 8.14 or higher

### Build Steps

1. Clone the repository
2. Open the project in Android Studio
3. Configure the sender email in `N26NotificationListener.java`
4. Build the project:
   ```bash
   C:\gradle\bin\gradle assembleDebug
   ```
5. The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

### Installation

1. Enable "Install from Unknown Sources" on your Android device
2. Transfer the APK to your device
3. Install the APK
4. Grant the required permissions when prompted

## Usage

1. After installation, open the app
2. Enter your email address where you want to receive notifications
3. Click "Save"
4. The app will now forward all N26 notifications to your email

## Troubleshooting

### Common Issues

1. **Notifications not being forwarded**
   - Ensure notification access is granted
   - Check if N26 app is installed and notifications are enabled
   - Verify email address is correctly saved

2. **Email not received**
   - Check spam folder
   - Verify email address is correct
   - Ensure device has internet connection
   - Verify sender Gmail account is properly configured
   - Check if App Password is correct

3. **App crashes**
   - Clear app data and cache
   - Reinstall the app
   - Ensure all permissions are granted

## Dependencies

The app uses the following main dependencies:
- androidx.appcompat:appcompat:1.3.1
- com.google.android.material:material:1.4.0
- androidx.constraintlayout:constraintlayout:2.1.0
- com.sun.mail:android-mail:1.6.7
- com.sun.mail:android-activation:1.6.7

## Security Notes

- The app only reads notifications from the N26 app
- Email credentials are not stored in the app
- All data is stored locally on the device
- No data is sent to any third-party servers except for the email notifications
- The sender Gmail account requires an App Password for security
- Never commit your actual Gmail credentials to version control

## Contributing

Feel free to submit issues and enhancement requests!