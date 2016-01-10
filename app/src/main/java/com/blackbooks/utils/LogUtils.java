package com.blackbooks.utils;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Log utility class.
 */
public final class LogUtils {

    /**
     * Log filename.
     */
    public static final String LOG_FILENAME = "BlackBooks.log";

    /**
     * Log TAG.
     */
    public static final String TAG = "com.blackbooks";

    /**
     * Private constructor.
     */
    private LogUtils() {
    }

    /**
     * Write the content of the log to a file in the application directory on the external storage drive.
     *
     * @param activity The calling activity.
     * @return The file where the log has been written.
     */
    public static File writeLogToFile(Activity activity) {
        File file = FileUtils.createFileInAppDir(LOG_FILENAME);
        if (file != null) {
            String fullName = file.getAbsolutePath();
            try {
                String[] cmd = new String[]{"logcat", "-f", fullName, "-v", "time", TAG + ":D", "*:S"};

                Runtime.getRuntime().exec(cmd);

                MediaScannerConnection.scanFile(activity, new String[]{fullName}, null, null);

            } catch (IOException e) {
                Log.e(LogUtils.TAG, "An error occurred while writing the log into a file.", e);
            }
        }
        return file;
    }
}
