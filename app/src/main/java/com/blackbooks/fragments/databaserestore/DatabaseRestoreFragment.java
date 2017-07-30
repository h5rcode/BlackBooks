package com.blackbooks.fragments.databaserestore;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import com.blackbooks.activities.FileChooserActivity;
import com.blackbooks.database.Database;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.ProgressDialogFragment;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.LogUtils;
import com.blackbooks.utils.VariableUtils;

import java.io.File;

/**
 * Database restore fragment.
 */
public final class DatabaseRestoreFragment extends Fragment implements ProgressDialogFragment.OnProgressDialogListener {

    private static final int REQUEST_CHOOSE_FILE = 0;
    private static final String TAG_PROGRESS_DIALOG_FRAGMENT = "TAG_PROGRESS_DIALOG_FRAGMENT";

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
        cancelAsyncTask();
    }

    /**
     * Cancel the asynchronous task.
     */
    private void cancelAsyncTask() {
        if (mDatabaseRestoreTask != null) {
            mDatabaseRestoreTask.cancel(true);
        }
    }

    /**
     * Start the database restore task.
     */
    private void startDatabaseRestore() {
        if (VariableUtils.getInstance().getBulkSearchRunning()) {
            Toast.makeText(getActivity(), getString(R.string.message_stop_background_search), Toast.LENGTH_LONG).show();
        } else {
            mDatabaseRestoreTask = new DatabaseRestoreTask();
            mDatabaseRestoreTask.execute();
        }
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

    @Override
    public void onCancel() {
        cancelAsyncTask();
        mButtonRestoreDatabase.setEnabled(true);
    }

    /**
     * The asynchronous task that will restore the application's database.
     */
    private final class DatabaseRestoreTask extends AsyncTask<Void, Void, Void> {

        private Integer mMessageId;
        private ProgressDialogFragment mProgressDialogFragment;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mButtonRestoreDatabase.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(LogUtils.TAG, "Restoring database.");

            mProgressDialogFragment = ProgressDialogFragment.newInstanceSpinner(
                    R.string.title_dialog_restore_database,
                    R.string.message_db_restore
            );
            mProgressDialogFragment.setTargetFragment(DatabaseRestoreFragment.this, 0);

            final FragmentManager fm = getFragmentManager();
            mProgressDialogFragment.show(fm, TAG_PROGRESS_DIALOG_FRAGMENT);

            boolean isBackupFileOk;
            SQLiteDatabase db = null;
            try {
                if (isCancelled()) {
                    Log.d(LogUtils.TAG, "Restore task cancelled, aborting.");
                    return null;
                }
                Log.d(LogUtils.TAG, "Opening the database dump to restore.");
                db = SQLiteDatabase.openDatabase(mBackupFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

                if (isCancelled()) {
                    Log.d(LogUtils.TAG, "Restore task cancelled, aborting.");
                    return null;
                }
                Log.d(LogUtils.TAG, "Checking the database dump integrity.");
                isBackupFileOk = db.isDatabaseIntegrityOk();

                if (isBackupFileOk) {
                    Log.d(LogUtils.TAG, "Database dump integrity check succeeded.");
                } else {
                    Log.d(LogUtils.TAG, "Database dump integrity check failed.");
                    mMessageId = R.string.message_db_restore_dump_integrity_check_failed;
                }

            } catch (SQLiteException e) {
                mMessageId = R.string.message_db_restore_could_not_open_dump;
                Log.w(LogUtils.TAG, "Could not open the database dump.", e);
                isBackupFileOk = false;
            } finally {
                if (db != null) {
                    db.close();
                }
            }

            if (isBackupFileOk) {
                Log.d(LogUtils.TAG, "Closing connections to the database to restore.");

                if (isCancelled()) {
                    Log.d(LogUtils.TAG, "Restore task cancelled, aborting.");
                    return null;
                }
                SQLiteHelper sqliteHelper = SQLiteHelper.getInstance();
                sqliteHelper.close();

                Activity activity = getActivity();
                File currentDB = activity.getDatabasePath(Database.NAME);

                try {
                    Log.d(LogUtils.TAG, "Replacing the database by the dump.");

                    boolean success = FileUtils.copy(mBackupFile, currentDB);

                    if (success) {
                        Log.d(LogUtils.TAG, "Database successfully restored.");
                        mMessageId = R.string.message_db_restore_success;
                    } else {
                        Log.d(LogUtils.TAG, "The database restoration failed.");
                        mMessageId = R.string.message_db_restore_success;
                    }

                } catch (InterruptedException e) {
                    Log.d(LogUtils.TAG, "The restoration was interrupted.");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mMessageId != null) {
                Toast.makeText(getActivity(), mMessageId, Toast.LENGTH_LONG).show();
            }

            mButtonRestoreDatabase.setEnabled(true);
            mProgressDialogFragment.dismiss();
        }
    }
}
