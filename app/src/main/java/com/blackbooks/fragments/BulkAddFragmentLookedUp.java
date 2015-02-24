package com.blackbooks.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.adapters.IsbnListAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Isbn;
import com.blackbooks.services.IsbnServices;

import java.util.List;

/**
 * A fragment to display the IBNs that have been looked up.
 */
public final class BulkAddFragmentLookedUp extends ListFragment {

    private IsbnListAdapter mIsbnListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mIsbnListAdapter = new IsbnListAdapter(getActivity());
        setListAdapter(mIsbnListAdapter);

        new IsbnListLookedUpLoadTask().execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bulk_add_looked_up, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;

        switch (item.getItemId()) {

            case R.id.bulkAddLookedUp_deleteAll:
                showDeleteAllConfirmDialog();
                result = true;
                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }

    /**
     * Delete al the ISBNs.
     */
    private void deleteAll() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        IsbnServices.deleteAllLookedUpIsbns(db);
        mIsbnListAdapter.clear();
        mIsbnListAdapter.notifyDataSetChanged();
    }

    /**
     * Show the delete confirm dialog.
     */
    private void showDeleteAllConfirmDialog() {

        new AlertDialog.Builder(this.getActivity()) //
                .setTitle(R.string.title_dialog_delete_isbns) //
                .setMessage(R.string.message_confirm_delete_looked_up_isbns) //
                .setPositiveButton(R.string.message_confirm_delete_looked_up_isbns_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BulkAddFragmentLookedUp.this.deleteAll();
                    }
                }) //
                .setNegativeButton(R.string.message_confirm_delete_looked_up_isbns_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing.
                    }
                }).show();
    }

    /**
     * A task to load the looked up ISBNs.
     */
    private final class IsbnListLookedUpLoadTask extends AsyncTask<Void, Void, List<Isbn>> {

        @Override
        protected List<Isbn> doInBackground(Void... params) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
            return IsbnServices.getIsbnListLookedUp(db);
        }

        @Override
        protected void onPostExecute(List<Isbn> isbns) {
            super.onPostExecute(isbns);
            mIsbnListAdapter.addAll(isbns);
            mIsbnListAdapter.notifyDataSetChanged();
        }
    }
}
