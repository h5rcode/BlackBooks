package com.blackbooks.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.database.Database;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.ConfirmDialogFragment;
import com.blackbooks.utils.LogUtils;

import java.io.File;

/**
 * Database delete fragment.
 */
public final class DatabaseDeleteFragment extends Fragment implements ConfirmDialogFragment.OnConfirmListener {

    private static final String TAG_CONFIRM_DIALOG = "TAG_CONFIRM_DIALOG";
    private static final String TAG_PROGRESS_DIALOG = "TAG_PROGRESS_DIALOG";

    private Button mButtonDeleteDatabase;

    private DatabaseDeleteTask mDatabaseDeleteTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_database_delete, container, false);

        mButtonDeleteDatabase = (Button) view.findViewById(R.id.databaseDelete_buttonDeleteDb);

        mButtonDeleteDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentActivity activity = DatabaseDeleteFragment.this.getActivity();
                final FragmentManager fm = activity.getSupportFragmentManager();
                final ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance(
                        R.string.title_dialog_delete_database,
                        R.string.message_confirm_delete_database,
                        R.string.message_confirm_delete_database_confirm_message,
                        R.string.message_confirm_delete_database_cancel,
                        R.string.message_confirm_delete_database_confirm
                );

                fragment.setTargetFragment(DatabaseDeleteFragment.this, 0);

                fragment.show(fm, TAG_CONFIRM_DIALOG);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabaseDeleteTask != null) {
            mDatabaseDeleteTask.cancel(true);
        }
    }

    @Override
    public void onConfirm() {
        mDatabaseDeleteTask = new DatabaseDeleteTask();
        mDatabaseDeleteTask.execute();
    }

    /**
     * The asynchronous task that will delete the application's database..
     */
    private final class DatabaseDeleteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mButtonDeleteDatabase.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(LogUtils.TAG, "Deleting database.");

            SQLiteHelper sqliteHelper = SQLiteHelper.getInstance();
            sqliteHelper.close();

            Activity activity = getActivity();
            File currentDB = activity.getDatabasePath(Database.NAME);
            return currentDB.delete();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Log.d(LogUtils.TAG, "Database successfully deleted.");
                Toast.makeText(getActivity(), R.string.message_db_delete_success, Toast.LENGTH_LONG).show();
            } else {
                Log.d(LogUtils.TAG, "The database could not be deleted.");
                Toast.makeText(getActivity(), R.string.message_db_delete_failure, Toast.LENGTH_LONG).show();
            }

            mButtonDeleteDatabase.setEnabled(true);
        }
    }
}