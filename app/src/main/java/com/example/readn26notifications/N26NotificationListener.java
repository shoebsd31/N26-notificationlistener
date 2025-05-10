package com.example.readn26notifications;

import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

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
    private static final String N26_PACKAGE = "de.number26.android";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.getPackageName().equals(N26_PACKAGE)) {
            return;
        }

        String notificationText = sbn.getNotification().extras.getString("android.text");
        if (notificationText == null) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences("N26NotificationPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");

        if (!email.isEmpty()) {
            sendEmail(email, notificationText);
            if (MainActivity.instance != null) {
                MainActivity.instance.addLog("Notification sent to " + email + ": " + notificationText);
            }
        }
    }

    private void sendEmail(String toEmail, String notificationText) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("your-email@gmail.com", "your-app-password");
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("your-email@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("N26 Notification");
                message.setText(notificationText);

                Transport.send(message);
                Log.d(TAG, "Email sent successfully");
            } catch (MessagingException e) {
                Log.e(TAG, "Error sending email", e);
            }
        }).start();
    }
} 