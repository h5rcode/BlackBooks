package com.blackbooks.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.utils.LogUtils;

import java.io.File;

/**
 * The activity that allows the user to report errors by sending the log file to
 * the developer by mail.
 */
public final class ReportErrorActivity extends Activity {

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
        File logFile = LogUtils.writeLogToFile(this);

        if (logFile == null) {
            Toast.makeText(this, R.string.report_error_log_not_saved, Toast.LENGTH_LONG).show();
        } else {
            String fullName = logFile.getAbsolutePath();
            startSendActivity(fullName);
        }
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
