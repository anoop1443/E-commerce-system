package com.example.deliveryboy.ringtoun; // अपना package name बदलें

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.deliveryboy.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class RingActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 101;
    private static final int REQUEST_RINGTONE_PICKER = 202;
    private static final String LOG_TAG = "RingtoneCopyApp";

    // यह variable store करेगा कि user ने किस type की tone चुनने के लिए button दबाया था।
    private int currentRingtoneType = 0;

    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        tvStatus = findViewById(R.id.tv_status);
        Button btnPickRingtone = findViewById(R.id.btn_pick_ringtone);
        Button btnPickAlarm = findViewById(R.id.btn_pick_alarm);
        Button btnPickNotification = findViewById(R.id.btn_pick_notification);

        // Call Ringtone के लिए Listener
        btnPickRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRingtoneType = RingtoneManager.TYPE_RINGTONE;
                checkPermissionsAndLaunchPicker("Call Ringtone", currentRingtoneType);
            }
        });

        // Alarm Tone के लिए Listener
        btnPickAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRingtoneType = RingtoneManager.TYPE_ALARM;
                checkPermissionsAndLaunchPicker("Alarm Tone", currentRingtoneType);
            }
        });

        // Notification Tone के लिए Listener
        btnPickNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRingtoneType = RingtoneManager.TYPE_NOTIFICATION;
                checkPermissionsAndLaunchPicker("Notification Tone", currentRingtoneType);
            }
        });
    }

    // --- 1. Permissions Check and Launch Picker ---

    private void checkPermissionsAndLaunchPicker(String title, int type) {
        // Android 10 (API 29) से पहले WRITE_EXTERNAL_STORAGE की आवश्यकता है।
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
                return;
            }
        }
        // अगर Permission ठीक है या API 29+ है, तो picker launch करें।
        launchRingtonePicker(title, type);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission मिलने के बाद, picker को relaunch करें
                launchRingtonePicker(getToneTitle(currentRingtoneType), currentRingtoneType);
            } else {
                Toast.makeText(this, "फ़ाइल कॉपी करने के लिए Storage permission ज़रूरी है।", Toast.LENGTH_LONG).show();
                tvStatus.setText("स्थिति: Permission नहीं दी गई।");
            }
        }
    }

    // type के आधार पर Picker का title return करता है।
    private String getToneTitle(int type) {
        if (type == RingtoneManager.TYPE_RINGTONE) return "Call Ringtone चुनें";
        if (type == RingtoneManager.TYPE_ALARM) return "Alarm Tone चुनें";
        if (type == RingtoneManager.TYPE_NOTIFICATION) return "Notification Tone चुनें";
        return "Tone चुनें";
    }


    // --- 2. Launch Ringtone Picker ---

    private void launchRingtonePicker(String title, int type) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

        // Ringtone type set करें (Ringtone, Alarm, या Notification)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, type);

        // Picker का title set करें
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, title);

        startActivityForResult(intent, REQUEST_RINGTONE_PICKER);
        tvStatus.setText("स्थिति: Tone Picker खोला गया...");
    }

    // --- 3. Handle Selected Ringtone URI ---

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RINGTONE_PICKER && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                tvStatus.setText("स्थिति: Tone URI मिली। कॉपी हो रही है...");

                // Copy करने के लिए background thread का उपयोग करें
                new Thread(() -> {
                    boolean success = copyRingtoneFile(this, uri, currentRingtoneType);

                    runOnUiThread(() -> {
                        if (success) {
                            tvStatus.setText("स्थिति: Tone सफलतापूर्वक Downloads folder में कॉपी हो गई!");
                            Toast.makeText(this, "Tone कॉपी हो गई!", Toast.LENGTH_LONG).show();
                        } else {
                            tvStatus.setText("स्थिति: Tone कॉपी करने में विफल रहा।");
                            Toast.makeText(this, "Tone कॉपी करने में विफल रहा।", Toast.LENGTH_LONG).show();
                        }
                    });
                }).start();
            } else {
                tvStatus.setText("स्थिति: कोई tone नहीं चुनी गई।");
            }
        } else if (requestCode == REQUEST_RINGTONE_PICKER && resultCode == RESULT_CANCELED) {
            tvStatus.setText("स्थिति: Tone चयन रद्द किया गया।");
        }
    }

    // --- 4. File Copy Logic (Type के साथ) ---

    private boolean copyRingtoneFile(Context context, Uri uri, int type) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        Cursor cursor = null;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e(LOG_TAG, "Cannot open input stream for URI: " + uri);
                return false;
            }

            // tone के type के आधार पर prefix तय करें
            String prefix;
            if (type == RingtoneManager.TYPE_RINGTONE) prefix = "CALL_";
            else if (type == RingtoneManager.TYPE_ALARM) prefix = "ALARM_";
            else if (type == RingtoneManager.TYPE_NOTIFICATION) prefix = "NOTIF_";
            else prefix = "AUDIO_";

            // Default file name
            String fileName = prefix + "copied_tone.mp3";

            // URI से real file name प्राप्त करने का प्रयास करें
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                        // Prefix जोड़ें और सुनिश्चित करें कि extension मौजूद है।
                        if (!fileName.contains(".")) {
                            fileName = prefix + fileName.replaceAll("[^a-zA-Z0-9-.]", "_") + ".mp3";
                        } else {
                            // अगर extension है, तो file name को clean करके prefix जोड़ें
                            String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
                            String ext = fileName.substring(fileName.lastIndexOf('.'));
                            fileName = prefix + nameWithoutExt.replaceAll("[^a-zA-Z0-9-]", "_") + ext;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not get file name from URI cursor: " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            // Destination directory: Public Downloads folder
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloadsDir == null) {
                Log.e(LOG_TAG, "Downloads directory is not available.");
                return false;
            }
            downloadsDir.mkdirs();

            File destinationFile = new File(downloadsDir, fileName);

            // Output Stream खोलें
            outputStream = new FileOutputStream(destinationFile);

            // Bytes copy करें
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            Log.i(LOG_TAG, "Tone copied successfully to: " + destinationFile.getAbsolutePath());

            // MediaScanner को सूचित करें
            MediaScannerConnection.scanFile(
                    context,
                    new String[]{destinationFile.getAbsolutePath()},
                    null,
                    (path, uri1) -> Log.i(LOG_TAG, "MediaScanner finished: " + path)
            );

            return true;

        } catch (IOException e) {
            Log.e(LOG_TAG, "File copy failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Streams बंद करें
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
