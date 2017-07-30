package com.blackbooks.fragments.bulkadd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.blackbooks.activities.BookDisplayActivity;
import com.blackbooks.adapters.LookedUpIsbnListAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Isbn;
import com.blackbooks.services.IsbnServices;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * A fragment to display the IBNs that have been looked up.
 */
public final class BulkAddFragmentLookedUp extends ListFragment {

    private static final int ISBNS_BY_PAGE = 50;

    private Integer mIsbnCount;
    private boolean mAlreadyLoaded;
    private int mLastPage = 1;
    private int mLastItem = -1;

    private LookedUpIsbnListAdapter mLookedUpIsbnListAdapter;
    private TextView mTextViewFooter;
    private IsbnListLookedUpLoadTask mIsbnListLookedUpLoadTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mLookedUpIsbnListAdapter = new LookedUpIsbnListAdapter(getActivity());
        setListAdapter(mLookedUpIsbnListAdapter);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bulk_add_looked_up, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFooterText();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mAlreadyLoaded || VariableUtils.getInstance().getReloadIsbnListLookedUp()) {
            mAlreadyLoaded = true;
            VariableUtils.getInstance().setReloadIsbnListLookedUp(false);
            reloadIsbns();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsbnListLookedUpLoadTask != null) {
            mIsbnListLookedUpLoadTask.cancel(true);
        }
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Isbn isbn = (Isbn) getListAdapter().getItem(position);
        if (isbn.bookId != null) {
            Intent i = new Intent(this.getActivity(), BookDisplayActivity.class);
            i.putExtra(BookDisplayActivity.EXTRA_BOOK_ID, isbn.bookId);
            this.startActivity(i);
        }
    }

    /**
     * Delete al the ISBNs.
     */
    private void deleteAll() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        IsbnServices.deleteAllLookedUpIsbns(db);
        reloadIsbns();
    }

    /**
     * Load more ISBNs.
     */
    private void loadMoreIsbns() {
        mIsbnListLookedUpLoadTask = new IsbnListLookedUpLoadTask(ISBNS_BY_PAGE, ISBNS_BY_PAGE * (mLastPage - 1));
        mIsbnListLookedUpLoadTask.execute();
        mLastPage++;
    }

    /**
     * Reload the list of ISBNs from the first page.
     */
    private void reloadIsbns() {
        mLookedUpIsbnListAdapter.clear();
        mLastItem = -1;
        mLastPage = 1;
        loadMoreIsbns();
    }


    /**
     * Set the footer text.
     */
    private void setFooterText() {
        if (mIsbnCount != null) {

            int displayedIsbnCount = mLookedUpIsbnListAdapter.getCount();

            Resources res = getResources();
            String footerText = res.getQuantityString(R.plurals.footer_fragment_bulk_add, displayedIsbnCount, displayedIsbnCount, mIsbnCount);

            mTextViewFooter.setText(footerText);
        }
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

        private final int mLimit;
        private final int mOffset;

        /**
         * Constructor.
         *
         * @param limit  Limit.
         * @param offset Offset.
         */
        public IsbnListLookedUpLoadTask(int limit, int offset) {
            mLimit = limit;
            mOffset = offset;
        }

        @Override
        protected List<Isbn> doInBackground(Void... params) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
            mIsbnCount = IsbnServices.getIsbnListLookedUpCount(db);
            return IsbnServices.getIsbnListLookedUp(db, mLimit, mOffset);
        }

        @Override
        protected void onPostExecute(List<Isbn> isbns) {
            super.onPostExecute(isbns);

            int initialIsbnCount = mLookedUpIsbnListAdapter.getCount();

            mLookedUpIsbnListAdapter.addAll(isbns);
            mLookedUpIsbnListAdapter.notifyDataSetChanged();

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
