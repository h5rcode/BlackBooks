package com.blackbooks;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.blackbooks.activities.ReportErrorActivity;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.utils.LogUtils;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Black Books application class.
 */
public final class BlackBooksApplication extends Application {

    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LogUtils.TAG, "Application starting.");

        final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Log.d(LogUtils.TAG, "Google Analytics dry run enabled: " + analytics.isDryRunEnabled());

        mTracker = analytics.newTracker(R.xml.tracker_config);

        SQLiteHelper.initialize(getApplicationContext());

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(e);
            }
        });
    }

    /**
     * Handle uncaught exception.
     *
     * @param e The exception that was thrown.
     */
    public void handleUncaughtException(Throwable e) {
        Log.e(LogUtils.TAG, "Uncaught exception.", e);

        Intent intent = new Intent(getApplicationContext(), ReportErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);

        System.exit(1);
    }
}
