package com.blackbooks.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.IsbnListAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Isbn;
import com.blackbooks.service.BulkSearchService;
import com.blackbooks.services.IsbnServices;
import com.blackbooks.utils.IsbnUtils;
import com.blackbooks.utils.Pic2ShopUtils;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * Bulk add fragment.
 */
public final class BulkAddFragment extends ListFragment {

    private IsbnListAdapter mIsbnListAdapter;
    private boolean mStartScan;

    /**
     * Return a new instance of BulkAddFragment.
     *
     * @return BulkAddFragment.
     */
    public static BulkAddFragment newInstance() {
        return new BulkAddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mIsbnListAdapter = new IsbnListAdapter(getActivity());
        setListAdapter(mIsbnListAdapter);

        new IsbnListLoadTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bulk_add, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bulk_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        Intent i;
        switch (item.getItemId()) {
            case R.id.bulkAdd_startScanning:
                startIsbnScan();
                result = true;
                break;

            case R.id.bulkAdd_startSearch:
                i = new Intent(getActivity(), BulkSearchService.class);
                getActivity().startService(i);
                result = true;
                break;

            case R.id.bulkAdd_stopSearch:
                i = new Intent(getActivity(), BulkSearchService.class);
                getActivity().stopService(i);
                result = true;
                break;

            case R.id.bulkAdd_deleteAll:
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
        IsbnServices.deleteAllIsbns(db);
        mIsbnListAdapter.clear();
        mIsbnListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mStartScan) {
            startIsbnScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Pic2ShopUtils.REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            String barCode = data.getStringExtra(Pic2ShopUtils.BARCODE);

            String message;
            if (IsbnUtils.isValidIsbn(barCode)) {
                SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
                IsbnServices.saveIsbn(db, barCode);
                message = getString(R.string.message_isbn_saved, barCode);
            } else {
                message = getString(R.string.message_invalid_isbn, barCode);
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            mStartScan = true;
        } else {
            mStartScan = false;
        }
    }

    /**
     * Launches Pic2Shop to start scanning an ISBN code.
     */
    private void startIsbnScan() {
        Intent intent = new Intent(Pic2ShopUtils.ACTION);
        startActivityForResult(intent, Pic2ShopUtils.REQUEST_CODE_SCAN);
    }

    /**
     * Show the delete confirm dialog.
     */
    private void showDeleteAllConfirmDialog() {
        if (VariableUtils.getInstance().getBulkSearchRunning()) {
            Toast.makeText(getActivity(), getString(R.string.message_cannot_delete_isbns), Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this.getActivity()) //
                .setTitle(R.string.title_dialog_delete_isbns) //
                .setMessage(R.string.message_confirm_delete_isbns) //
                .setPositiveButton(R.string.message_confirm_delete_isbns_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BulkAddFragment.this.deleteAll();
                    }
                }) //
                .setNegativeButton(R.string.message_confirm_delete_isbns_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing.
                    }
                }).show();
    }

    /**
     * A task to load the ISBNs.
     */
    private final class IsbnListLoadTask extends AsyncTask<Void, Void, List<Isbn>> {

        @Override
        protected List<Isbn> doInBackground(Void... params) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
            return IsbnServices.getIsbnListToLookUp(db);
        }

        @Override
        protected void onPostExecute(List<Isbn> isbns) {
            super.onPostExecute(isbns);
            mIsbnListAdapter.addAll(isbns);
            mIsbnListAdapter.notifyDataSetChanged();
        }
    }
}
