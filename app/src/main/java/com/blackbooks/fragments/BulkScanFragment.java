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
import com.blackbooks.adapters.ScannedIsbnsAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.ScannedIsbn;
import com.blackbooks.service.BulkSearchService;
import com.blackbooks.services.ScannedIsbnServices;
import com.blackbooks.utils.IsbnUtils;
import com.blackbooks.utils.Pic2ShopUtils;

import java.util.List;

/**
 * Bulk scan fragment.
 */
public final class BulkScanFragment extends ListFragment {

    private ScannedIsbnsAdapter mScannedIsbnsAdapter;
    private boolean mStartScan;

    /**
     * Return a new instance of BulkScanFragment.
     *
     * @return BulkScanFragment.
     */
    public static BulkScanFragment newInstance() {
        return new BulkScanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mScannedIsbnsAdapter = new ScannedIsbnsAdapter(getActivity());
        setListAdapter(mScannedIsbnsAdapter);

        new ScannedIsbnsLoadTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bulk_scan, container, false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bulk_scan, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        Intent i;
        switch (item.getItemId()) {
            case R.id.bulkScan_startScanning:
                startIsbnScan();
                result = true;
                break;

            case R.id.bulkScan_startSearch:
                i = new Intent(getActivity(), BulkSearchService.class);
                getActivity().startService(i);
                result = true;
                break;

            case R.id.bulkScan_stopSearch:
                i = new Intent(getActivity(), BulkSearchService.class);
                getActivity().stopService(i);
                result = true;
                break;

            case R.id.bulkScan_deleteAll:
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
     * Delete al the scanned ISBNs.
     */
    private void deleteAll() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        ScannedIsbnServices.deleteAllScannedIsbns(db);
        mScannedIsbnsAdapter.clear();
        mScannedIsbnsAdapter.notifyDataSetChanged();
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
                ScannedIsbnServices.saveScannedIsbn(db, barCode);
                message = getString(R.string.message_scanned_isbn_saved, barCode);
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
        new AlertDialog.Builder(this.getActivity()) //
                .setTitle(R.string.title_dialog_delete_scanned_isbns) //
                .setMessage(R.string.message_confirm_delete_scanned_isbns) //
                .setPositiveButton(R.string.message_confirm_delete_scanned_isbns_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BulkScanFragment.this.deleteAll();
                    }
                }) //
                .setNegativeButton(R.string.message_confirm_delete_scanned_isbns_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing.
                    }
                }).show();
    }

    /**
     * A task to load the scanned ISBNs.
     */
    private final class ScannedIsbnsLoadTask extends AsyncTask<Void, Void, List<ScannedIsbn>> {

        @Override
        protected List<ScannedIsbn> doInBackground(Void... params) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
            return ScannedIsbnServices.getScannedIsbnList(db);
        }

        @Override
        protected void onPostExecute(List<ScannedIsbn> scannedIsbns) {
            super.onPostExecute(scannedIsbns);
            mScannedIsbnsAdapter.addAll(scannedIsbns);
            mScannedIsbnsAdapter.notifyDataSetChanged();
        }
    }
}
