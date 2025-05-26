package com.example.readn26notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Base64;

import androidx.core.app.NotificationCompat;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class N26NotificationListener extends NotificationListenerService {
    private static final String TAG = "N26NotificationListener";
    private static final String N26_PACKAGE = "com.whatsapp";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "N26ListenerChannel";
    private static final String APPLICATION_NAME = "WhatsApp Notification Listener";
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground();
        if (MainActivity.instance != null) {
            MainActivity.instance.addLog("Notification listener service started");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "N26 Notification Listener",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Keeps the N26 notification listener running");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                if (MainActivity.instance != null) {
                    MainActivity.instance.addLog("Notification channel created");
                }
            }
        }
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    0
            );
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("N26 Notification Listener")
                .setContentText("Listening for N26 notifications")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(NOTIFICATION_ID, notification);
        if (MainActivity.instance != null) {
            MainActivity.instance.addLog("Service started in foreground mode");
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "Notification received from package: " + sbn.getPackageName());
        
        if (!sbn.getPackageName().equals(N26_PACKAGE)) {
            Log.d(TAG, "Ignoring notification from non-WhatsApp package");
            return;
        }

        String notificationText = sbn.getNotification().extras.getString("android.text");
        if (notificationText == null) {
            if (MainActivity.instance != null) {
                MainActivity.instance.addLog("Received WhatsApp notification but text was null");
            }
            Log.d(TAG, "Notification text was null");
            return;
        }

        if (MainActivity.instance != null) {
            MainActivity.instance.addLog("Received WhatsApp notification: " + notificationText);
        }
        Log.d(TAG, "Attempting to send email for notification: " + notificationText);

        sendEmail("shoebhasansayyed@gmail.com", notificationText);
    }

    private void sendEmail(String toEmail, String notificationText) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting email sending process");
                
                // Load credentials from the credentials file
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                    getAssets().open(CREDENTIALS_FILE_PATH))
                    .createScoped("https://www.googleapis.com/auth/gmail.send");

                // Create Gmail service
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                Gmail service = new Gmail.Builder(HTTP_TRANSPORT, 
                    GsonFactory.getDefaultInstance(), 
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

                // Create email message
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage email = new MimeMessage(session);
                email.setFrom(new InternetAddress("shoebsd31@gmail.com"));
                email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
                email.setSubject("WhatsApp Notification");
                email.setText(notificationText);

                // Encode the email
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                email.writeTo(buffer);
                byte[] bytes = buffer.toByteArray();
                String encodedEmail = Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP);

                // Create the message
                Message message = new Message();
                message.setRaw(encodedEmail);

                // Send the message
                Log.d(TAG, "Sending email");
                service.users().messages().send("me", message).execute();
                
                Log.d(TAG, "Email sent successfully");
                if (MainActivity.instance != null) {
                    MainActivity.instance.addLog("Email sent successfully to " + toEmail);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending email", e);
                if (MainActivity.instance != null) {
                    MainActivity.instance.addLog("Error sending email: " + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (MainActivity.instance != null) {
            MainActivity.instance.addLog("Notification listener service stopped");
        }
    }
} 