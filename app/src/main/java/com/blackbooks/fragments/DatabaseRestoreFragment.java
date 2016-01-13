package com.blackbooks.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.FileChooserActivity;
import com.blackbooks.database.Database;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.LogUtils;

import java.io.File;

/**
 * Database restore fragment.
 */
public final class DatabaseRestoreFragment extends Fragment {

    private static final int REQUEST_CHOOSE_FILE = 0;

    private Button mButtonSelectFile;
    private Button mButtonRestoreDatabase;

    private File mBackupFile;

    private DatabaseRestoreTask mDatabaseRestoreTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_database_restore, container, false);

        mButtonSelectFile = (Button) view.findViewById(R.id.databaseRestore_buttonSelectFile);
        mButtonRestoreDatabase = (Button) view.findViewById(R.id.databaseRestore_buttonRestoreDb);

        mButtonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FileChooserActivity.class);
                startActivityForResult(i, REQUEST_CHOOSE_FILE);
            }
        });

        mButtonRestoreDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseRestoreFragment.this.startDatabaseRestore();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabaseRestoreTask != null) {
            mDatabaseRestoreTask.cancel(true);
        }
    }

    /**
     * Start the database restore task.
     */
    private void startDatabaseRestore() {
        mDatabaseRestoreTask = new DatabaseRestoreTask();
        mDatabaseRestoreTask.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHOOSE_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                File file = (File) data.getSerializableExtra(FileChooserActivity.EXTRA_CHOSEN_FILE);
                mBackupFile = file;
                mButtonSelectFile.setText(file.getName());
                mButtonRestoreDatabase.setEnabled(true);
            }
        }
    }

    /**
     * The asynchronous task that will restore the application's database.
     */
    private final class DatabaseRestoreTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mButtonRestoreDatabase.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(LogUtils.TAG, "Restoring database.");

            boolean isBackupFileOk;
            try {
                Log.d(LogUtils.TAG, "Opening the database dump to restore.");
                SQLiteDatabase db = SQLiteDatabase.openDatabase(mBackupFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

                Log.d(LogUtils.TAG, "Checking the database dump integrity.");
                isBackupFileOk = db.isDatabaseIntegrityOk();
            } catch (SQLiteException e) {
                Log.w(LogUtils.TAG, "Could not open the database dump.", e);
                isBackupFileOk = false;
            }

            boolean success;
            if (isBackupFileOk) {
                Log.d(LogUtils.TAG, "Database dump integrity check succeeded.");
                Log.d(LogUtils.TAG, "Closing connections to the database to restore.");

                SQLiteHelper sqliteHelper = SQLiteHelper.getInstance();
                sqliteHelper.close();

                Activity activity = getActivity();
                File currentDB = activity.getDatabasePath(Database.NAME);

                try {
                    Log.d(LogUtils.TAG, "Replacing the database by the dump.");

                    success = FileUtils.copy(mBackupFile, currentDB);
                } catch (InterruptedException e) {
                    success = false;
                }
            } else {
                Log.d(LogUtils.TAG, "Database dump integrity check failed.");
                success = false;
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Log.d(LogUtils.TAG, "Database successfully restored.");
                Toast.makeText(getActivity(), "Database successfully restored.", Toast.LENGTH_LONG).show();
            } else {
                Log.d(LogUtils.TAG, "The database could not be restored.");
                Toast.makeText(getActivity(), "The database could not be restored.", Toast.LENGTH_LONG).show();
            }

            mButtonRestoreDatabase.setEnabled(true);
        }
    }
}
