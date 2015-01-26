package com.blackbooks.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.LogUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The activity that allows the user to report errors by sending the log file to
 * the developer by mail.
 */
public class ReportErrorActivity extends Activity {

    private static final String EMAIL = "report.blackbooks@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_error);

        Button buttonSendLog = (Button) findViewById(R.id.reportError_buttonSendLog);
        Button buttonCancel = (Button) findViewById(R.id.reportError_buttonCancel);

        buttonSendLog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendLog();
            }
        });

        buttonCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ReportErrorActivity.this.finish();
            }
        });
    }

    /**
     * Extract the log to a file and start the activity to send it.
     */
    private void sendLog() {
        String fullName = extractLogToFile();

        if (fullName == null) {
            Toast.makeText(this, R.string.report_error_log_not_saved, Toast.LENGTH_LONG).show();
        } else {
            startSendActivity(fullName);
        }
    }

    private String extractLogToFile() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.e(LogUtils.TAG, "Could not get package info.", e);
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER)) {
            model = Build.MANUFACTURER + " " + model;
        }

        File file = FileUtils.createFileInAppDir("Error.log");
        String fullName = null;
        if (file != null) {
            fullName = file.getAbsolutePath();
            InputStreamReader reader = null;
            FileWriter writer = null;
            try {
                String cmd = "logcat -d -v time " + LogUtils.TAG + ":v dalvikvm:v System.err:v *:s";

                Process process = Runtime.getRuntime().exec(cmd);
                reader = new InputStreamReader(process.getInputStream());

                writer = new FileWriter(file);
                writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
                writer.write("Device: " + model + "\n");
                writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

                char[] buffer = new char[10000];
                boolean done = false;
                do {
                    int n = reader.read(buffer, 0, buffer.length);
                    if (n == -1) {
                        done = true;
                    } else {
                        writer.write(buffer, 0, n);
                    }
                } while (!done);

                reader.close();
                writer.close();

                MediaScannerConnection.scanFile(this, new String[]{fullName}, null, null);

            } catch (IOException e) {
                Log.e(LogUtils.TAG, "An error occured while writing the log into a file.", e);
                if (writer != null)
                    try {
                        writer.close();
                    } catch (IOException e1) {
                        Log.e(LogUtils.TAG, "The writer could not be closed.", e);
                    }
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        Log.e(LogUtils.TAG, "The reader could not be closed.", e);
                    }

                fullName = null;
            }

        }
        return fullName;
    }

    /**
     * Start the activity that will be used to send the log file.
     *
     * @param fullName The full name of the log file.
     */
    private void startSendActivity(String fullName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_error_mail_subject));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fullName));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.report_error_mail_content));
        startActivity(intent);
    }
}
