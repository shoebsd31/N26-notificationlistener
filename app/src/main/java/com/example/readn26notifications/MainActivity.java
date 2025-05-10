package com.example.readn26notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    private TextInputEditText emailInput;
    private TextView logsText;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "N26NotificationPrefs";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        emailInput = findViewById(R.id.emailInput);
        logsText = findViewById(R.id.logsText);
        Button saveButton = findViewById(R.id.saveButton);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        emailInput.setText(savedEmail);

        saveButton.setOnClickListener(v -> saveEmail());

        // Request notification access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "N26NotificationChannel",
                    "N26 Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void saveEmail() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();

        Toast.makeText(this, "Email saved successfully", Toast.LENGTH_SHORT).show();
        addLog("Email saved: " + email);
    }

    public void addLog(String message) {
        runOnUiThread(() -> {
            String currentLogs = logsText.getText().toString();
            String newLog = currentLogs.isEmpty() ? message : currentLogs + "\n" + message;
            logsText.setText(newLog);
        });
    }

    public static void appendLog(SharedPreferences prefs, String message) {
        if (instance != null) {
            instance.addLog(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
