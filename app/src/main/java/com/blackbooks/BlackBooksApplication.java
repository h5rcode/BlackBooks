package com.blackbooks;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.blackbooks.activities.ReportErrorActivity;
import com.blackbooks.utils.LogUtils;

/**
 * Black Books application class.
 */
public final class BlackBooksApplication extends Application {

    @Override
    public void onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });
    }

    /**
     * Handle uncaught exception.
     *
     * @param thread The thread that has an uncaught exception.
     * @param e      The exception that was thrown.
     */
    public void handleUncaughtException(Thread thread, Throwable e) {
        Log.e(LogUtils.TAG, "Uncaught exception.", e);

        Intent intent = new Intent(getApplicationContext(), ReportErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);

        System.exit(1);
    }
}
