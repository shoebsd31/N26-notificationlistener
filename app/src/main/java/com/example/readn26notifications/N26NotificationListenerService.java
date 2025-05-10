package com.example.readn26notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class N26NotificationListenerService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn.getPackageName().equals("de.number26.android")) {
            String content = sbn.getNotification().extras.getString("android.text");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String email = prefs.getString("email", "");
            if (!email.isEmpty() && content != null) {
                EmailSender.sendEmail(this, email, "N26 Notification", content);
                MainActivity.appendLog(prefs, "Sent to: " + email + " | Content: " + content);
            }
        }
    }
}
