package com.blackbooks.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.PendingIsbnListAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.IsbnAddFragment;
import com.blackbooks.fragments.dialogs.ScannerInstallFragment;
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
public final class BulkAddFragmentPending extends ListFragment implements IsbnAddFragment.IsbnAddListener {

    private static final int ISBNS_BY_PAGE = 50;

    private static final String TAG_SCANNER_INSTALL_FRAGMENT = "TAG_SCANNER_INSTALL_FRAGMENT";
    private static final String TAG_ISBN_ADD_FRAGMENT = "TAG_ISBN_ADD_FRAGMENT";

    private Integer mIsbnCount;
    private boolean mAlreadyLoaded;
    private int mLastPage = 1;
    private int mLastItem = -1;

    private PendingIsbnListAdapter mPendingIsbnListAdapter;
    private TextView mTextViewFooter;
    private String mBulkScanMessage;
    private IsbnListLoadTask mIsbnListLoadTask;

    /**
     * Return a new instance of BulkAddFragment.
     *
     * @return BulkAddFragment.
     */
    public static BulkAddFragmentPending newInstance() {
        return new BulkAddFragmentPending();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mPendingIsbnListAdapter = new PendingIsbnListAdapter(getActivity());
        setListAdapter(mPendingIsbnListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bulk_add, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);

        mTextViewFooter = (TextView) view.findViewById(R.id.bulkAdd_textFooter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Do nothing.
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount == 0) {
                    return;
                }
                switch (view.getId()) {
                    case android.R.id.list:

                        final int lastItem = firstVisibleItem + visibleItemCount;
                        if (lastItem == totalItemCount) {
                            if (mLastItem != lastItem) {
                                mLastItem = lastItem;
                                loadMoreIsbns();
                            }
                        }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFooterText();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bulk_add_pending, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsbnListLoadTask != null) {
            mIsbnListLoadTask.cancel(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        Intent i;
        switch (item.getItemId()) {
            case R.id.bulkAddPending_startScanning:
                startIsbnScan();
                result = true;
                break;

            case R.id.bulkAddPending_enterIsbn:
                IsbnAddFragment isbnAddFragment = new IsbnAddFragment();
                isbnAddFragment.setTargetFragment(this, 0);
                isbnAddFragment.show(getFragmentManager(), TAG_ISBN_ADD_FRAGMENT);
                result = true;
                break;

            case R.id.bulkAddPending_startSearch:
                i = new Intent(getActivity(), BulkSearchService.class);
                getActivity().startService(i);
                result = true;
                break;

            case R.id.bulkAddPending_stopSearch:
                i = new Intent(getActivity(), BulkSearchService.class);
                getActivity().stopService(i);
                result = true;
                break;

            case R.id.bulkAddPending_deleteAll:
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
        IsbnServices.deleteAllPendingIsbns(db);
        reloadIsbns();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mAlreadyLoaded || VariableUtils.getInstance().getReloadIsbnListPending()) {
            mAlreadyLoaded = true;
            VariableUtils.getInstance().setReloadIsbnListPending(false);
            reloadIsbns();
        }

        if (mBulkScanMessage != null) {

            String message = getString(R.string.message_confirm_bulk_scan, mBulkScanMessage);

            new AlertDialog.Builder(this.getActivity()) //
                    .setTitle(R.string.title_dialog_bulk_scan) //
                    .setMessage(message) //
                    .setPositiveButton(R.string.message_confirm_bulk_scan_confirm, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startIsbnScan();
                        }
                    }) //
                    .setNegativeButton(R.string.message_confirm_bulk_scan_cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing.
                        }
                    }).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Pic2ShopUtils.REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                String barCode = data.getStringExtra(Pic2ShopUtils.BARCODE);

                String message;
                if (IsbnUtils.isValidIsbn(barCode)) {
                    saveIsbn(barCode);
                    message = getString(R.string.message_isbn_saved, barCode);
                } else {
                    message = getString(R.string.message_invalid_isbn, barCode);
                }
                mBulkScanMessage = message;
            } else {
                mBulkScanMessage = null;
            }
        }
    }


    /**
     * Load more ISBNs.
     */
    private void loadMoreIsbns() {
        mIsbnListLoadTask = new IsbnListLoadTask(ISBNS_BY_PAGE, ISBNS_BY_PAGE * (mLastPage - 1));
        mIsbnListLoadTask.execute();
        mLastPage++;
    }

    /**
     * Save an ISBN.
     *
     * @param isbn ISBN.
     */
    private void saveIsbn(String isbn) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        IsbnServices.saveIsbn(db, isbn);

        reloadIsbns();
    }

    /**
     * Reload the list of ISBNs from the first page.
     */
    private void reloadIsbns() {
        mPendingIsbnListAdapter.clear();
        mLastItem = -1;
        mLastPage = 1;
        loadMoreIsbns();
    }

    /**
     * Set the footer text.
     */
    private void setFooterText() {
        if (mIsbnCount != null) {

            int displayedIsbnCount = mPendingIsbnListAdapter.getCount();

            Resources res = getResources();
            String footerText = res.getQuantityString(R.plurals.footer_fragment_bulk_add, displayedIsbnCount, displayedIsbnCount, mIsbnCount);

            mTextViewFooter.setText(footerText);
        }
    }

    /**
     * Launches Pic2Shop to start scanning an ISBN code.
     */
    private void startIsbnScan() {
        Intent intent = new Intent(Pic2ShopUtils.ACTION);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo.isEmpty()) {
            FragmentManager fm = getFragmentManager();
            ScannerInstallFragment fragment = new ScannerInstallFragment();
            fragment.show(fm, TAG_SCANNER_INSTALL_FRAGMENT);
        } else {
            startActivityForResult(intent, Pic2ShopUtils.REQUEST_CODE_SCAN);
        }
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
                .setMessage(R.string.message_confirm_delete_pending_isbns) //
                .setPositiveButton(R.string.message_confirm_delete_pending_isbns_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BulkAddFragmentPending.this.deleteAll();
                    }
                }) //
                .setNegativeButton(R.string.message_confirm_delete_pending_isbns_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing.
                    }
                }).show();
    }

    @Override
    public void onAddIsbn(String isbn) {
        saveIsbn(isbn);
    }

    /**
     * A task to load the ISBNs.
     */
    private final class IsbnListLoadTask extends AsyncTask<Void, Void, List<Isbn>> {

        private final int mLimit;
        private final int mOffset;

        /**
         * Constructor.
         *
         * @param limit  Limit.
         * @param offset Offset.
         */
        public IsbnListLoadTask(int limit, int offset) {
            mLimit = limit;
            mOffset = offset;
        }

        @Override
        protected List<Isbn> doInBackground(Void... params) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
            mIsbnCount = IsbnServices.getIsbnListToLookUpCount(db);
            return IsbnServices.getIsbnListToLookUp(db, mLimit, mOffset);
        }

        @Override
        protected void onPostExecute(List<Isbn> isbns) {
            super.onPostExecute(isbns);

            int initialIsbnCount = mPendingIsbnListAdapter.getCount();

            mPendingIsbnListAdapter.addAll(isbns);
            mPendingIsbnListAdapter.notifyDataSetChanged();

            int isbnCont = isbns.size();
            if (isbnCont > 0 && initialIsbnCount > 0) {
                Resources res = getResources();
                String message = res.getQuantityString(R.plurals.message_isbns_loaded, isbnCont, isbnCont);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
            setFooterText();
        }
    }
}
