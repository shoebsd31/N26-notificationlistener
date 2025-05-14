package com.example.readn26notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    private TextInputEditText emailInput;
    private TextView logsText;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "N26NotificationPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGS = "notification_logs";
    private static final String KEY_LOG_TIMESTAMPS = "log_timestamps";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        emailInput = findViewById(R.id.emailInput);
        logsText = findViewById(R.id.logsText);
        Button saveButton = findViewById(R.id.saveButton);
        Button clearLogsButton = findViewById(R.id.clearLogsButton);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        emailInput.setText(savedEmail);

        saveButton.setOnClickListener(v -> saveEmail());
        clearLogsButton.setOnClickListener(v -> clearLogs());

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

        // Check and request notification access
        if (!isNotificationServiceEnabled()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Please enable notification access for this app", Toast.LENGTH_LONG).show();
        }

        // Load and display logs
        loadAndDisplayLogs();
    }

    private void loadAndDisplayLogs() {
        String logs = sharedPreferences.getString(KEY_LOGS, "");
        String timestamps = sharedPreferences.getString(KEY_LOG_TIMESTAMPS, "");
        
        if (!logs.isEmpty() && !timestamps.isEmpty()) {
            String[] logArray = logs.split("\\|\\|");
            String[] timestampArray = timestamps.split("\\|\\|");
            
            StringBuilder displayLogs = new StringBuilder();
            List<String> validLogs = new ArrayList<>();
            List<String> validTimestamps = new ArrayList<>();
            
            Calendar sevenDaysAgo = Calendar.getInstance();
            sevenDaysAgo.add(Calendar.DAY_OF_YEAR, -7);
            
            for (int i = 0; i < logArray.length; i++) {
                try {
                    Date logDate = DATE_FORMAT.parse(timestampArray[i]);
                    if (logDate != null && logDate.after(sevenDaysAgo.getTime())) {
                        validLogs.add(logArray[i]);
                        validTimestamps.add(timestampArray[i]);
                        displayLogs.append(timestampArray[i]).append(": ").append(logArray[i]).append("\n");
                    }
                } catch (Exception e) {
                    // Skip invalid timestamps
                }
            }
            
            // Save cleaned logs
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LOGS, String.join("||", validLogs));
            editor.putString(KEY_LOG_TIMESTAMPS, String.join("||", validTimestamps));
            editor.apply();
            
            logsText.setText(displayLogs.toString());
        }
    }

    private void clearLogs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGS);
        editor.remove(KEY_LOG_TIMESTAMPS);
        editor.apply();
        logsText.setText("");
        Toast.makeText(this, "Logs cleared", Toast.LENGTH_SHORT).show();
    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (flat != null && !flat.isEmpty()) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null && pkgName.equals(cn.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
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
            String timestamp = DATE_FORMAT.format(new Date());
            String currentLogs = sharedPreferences.getString(KEY_LOGS, "");
            String currentTimestamps = sharedPreferences.getString(KEY_LOG_TIMESTAMPS, "");
            
            String newLog = currentLogs.isEmpty() ? message : currentLogs + "||" + message;
            String newTimestamps = currentTimestamps.isEmpty() ? timestamp : currentTimestamps + "||" + timestamp;
            
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LOGS, newLog);
            editor.putString(KEY_LOG_TIMESTAMPS, newTimestamps);
            editor.apply();
            
            String displayLog = timestamp + ": " + message;
            String currentDisplayLogs = logsText.getText().toString();
            String newDisplayLog = currentDisplayLogs.isEmpty() ? displayLog : currentDisplayLogs + "\n" + displayLog;
            logsText.setText(newDisplayLog);
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
