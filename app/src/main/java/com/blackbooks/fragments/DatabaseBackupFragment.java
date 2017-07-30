package com.blackbooks.fragments;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.database.Database;
import com.blackbooks.fragments.dialogs.ProgressDialogFragment;
import com.blackbooks.fragments.dialogs.ProgressDialogFragment.OnProgressDialogListener;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.LogUtils;
import com.blackbooks.utils.VariableUtils;

import java.io.File;

/**
 * Database backup fragment.
 */
public final class DatabaseBackupFragment extends Fragment implements OnProgressDialogListener {

    private static final String TAG_PROGRESS_DIALOG_FRAGMENT = "TAG_PROGRESS_DIALOG_FRAGMENT";
    private Button mButtonBackupDatabase;

    private DatabaseBackupTask mDatabaseBackupTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_database_backup, container, false);

        mButtonBackupDatabase = (Button) view.findViewById(R.id.databaseBackup_buttonBackupDb);

        mButtonBackupDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseBackupFragment.this.startDatabaseBackup();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAsyncTask();
    }

    /**
     * Cancel the asynchronous task.
     */
    private void cancelAsyncTask() {
        if (mDatabaseBackupTask != null) {
            mDatabaseBackupTask.cancel(true);
        }
    }

    /**
     * Start the database backup task.
     */
    private void startDatabaseBackup() {
        if (VariableUtils.getInstance().getBulkSearchRunning()) {
            Toast.makeText(getActivity(), getString(R.string.message_stop_background_search), Toast.LENGTH_LONG).show();
        } else {
            mDatabaseBackupTask = new DatabaseBackupTask();
            mDatabaseBackupTask.execute();
        }
    }

    @Override
    public void onCancel() {
        cancelAsyncTask();
        mButtonBackupDatabase.setEnabled(true);
    }

    /**
     * The asynchronous task that will create a backup file of the app's database.
     */
    private final class DatabaseBackupTask extends AsyncTask<Void, Void, Boolean> {

        private File mDatabaseBackup;
        private ProgressDialogFragment mProgressDialogFragment;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mButtonBackupDatabase.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(LogUtils.TAG, "Saving a backup of the app's database.");

            mProgressDialogFragment = ProgressDialogFragment.newInstanceSpinner(
                    R.string.title_dialog_backup_database,
                    R.string.message_db_backup
            );
            mProgressDialogFragment.setTargetFragment(DatabaseBackupFragment.this, 0);

            final FragmentManager fm = getFragmentManager();
            mProgressDialogFragment.show(fm, TAG_PROGRESS_DIALOG_FRAGMENT);

            boolean success = false;
            final Activity activity = getActivity();
            final File currentDB = activity.getDatabasePath(Database.NAME);

            if (currentDB == null || !currentDB.exists()) {
                Log.d(LogUtils.TAG, "No database file to backup.");
            } else {
                mDatabaseBackup = FileUtils.createFileInAppDir(Database.NAME + ".sqlite");

                if (mDatabaseBackup == null) {
                    Log.d(LogUtils.TAG, "Could not create backup file.");
                } else {
                    try {

                        success = FileUtils.copy(currentDB, mDatabaseBackup);
                    } catch (InterruptedException e) {
                        Log.d(LogUtils.TAG, "Backup interrupted, aborting.");
                    }
                }
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Log.d(LogUtils.TAG, "App's database backup successful.");
                MediaScannerConnection.scanFile(getActivity(), new String[]{mDatabaseBackup.getAbsolutePath()}, null,
                        null);
                String message = String.format(getString(R.string.message_file_saved), mDatabaseBackup.getName(), mDatabaseBackup
                        .getParentFile().getName());
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else {
                Log.d(LogUtils.TAG, "App's database backup failed.");
                Toast.makeText(getActivity(), R.string.message_file_not_saved, Toast.LENGTH_LONG).show();
            }

            mButtonBackupDatabase.setEnabled(true);
            mProgressDialogFragment.dismiss();
        }
    }
}
