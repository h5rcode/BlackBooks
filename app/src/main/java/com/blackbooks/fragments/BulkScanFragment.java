package com.blackbooks.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
public class BulkScanFragment extends ListFragment {

    private ListView mListView;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bulk_scan, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
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

        switch (item.getItemId()) {
            case R.id.bulkScan_startScanning:
                startIsbnScan();
                result = true;
                break;

            case R.id.bulkScan_startSearch:
                Intent i = new Intent(getActivity(), BulkSearchService.class);
                getActivity().startService(i);
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
        loadScannedISBNs();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mStartScan) {
            startIsbnScan();
        } else {
            loadScannedISBNs();
        }
    }

    /**
     * Load the scanned ISBNs and display them in a ListView.
     */
    private void loadScannedISBNs() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        List<ScannedIsbn> scannedIsbnList = ScannedIsbnServices.getScannedIsbnList(db);

        ScannedIsbnsAdapter adapter = new ScannedIsbnsAdapter(getActivity(), scannedIsbnList);
        mListView.setAdapter(adapter);
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
}
