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

import androidx.core.app.NotificationCompat;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class N26NotificationListener extends NotificationListenerService {
    private static final String TAG = "N26NotificationListener";
    private static final String N26_PACKAGE = "com.whatsapp";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "N26ListenerChannel";

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
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");

                Log.d(TAG, "Creating mail session");
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        Log.d(TAG, "Authenticating with Gmail");
                        return new PasswordAuthentication("shoebsd31@gmail.com", "<some-password>");
                    }
                });

                Log.d(TAG, "Creating email message");
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("shoebsd31@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("WhatsApp Notification");
                message.setText(notificationText);

                Log.d(TAG, "Sending email");
                Transport.send(message);
                Log.d(TAG, "Email sent successfully");
                if (MainActivity.instance != null) {
                    MainActivity.instance.addLog("Email sent successfully to " + toEmail);
                }
            } catch (MessagingException e) {
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