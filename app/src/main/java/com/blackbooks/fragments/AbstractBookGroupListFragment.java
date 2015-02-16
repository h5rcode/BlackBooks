package com.blackbooks.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookListActivity2;
import com.blackbooks.adapters.BookGroupListAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * A fragment to display the book groups of a certain type.
 */
public abstract class AbstractBookGroupListFragment extends ListFragment {

    private static final int GROUPS_BY_PAGE = 50;

    private GroupLoadTask mGroupLoadTask;

    private boolean mAlreadyLoaded;
    private Integer mBookGroupCount;
    private int mLastPage = 1;
    private int mLastItem = -1;

    private BookGroupListAdapter mBookGroupListAdapter;

    private TextView mTextViewFooter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mBookGroupListAdapter = new BookGroupListAdapter(getActivity());
        setListAdapter(mBookGroupListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.abstract_book_group_list_fragment, container, false);

        ListView listView = (ListView) view.findViewById(android.R.id.list);

        mTextViewFooter = (TextView) view.findViewById(R.id.abstractBookGroupList_textFooter);

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
                                loadMoreBookGroups();
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
    public void onResume() {
        super.onResume();

        if (!mAlreadyLoaded || VariableUtils.getInstance().getReloadBookGroupList()) {
            mAlreadyLoaded = true;
            VariableUtils.getInstance().setReloadBookGroupListToFalse();
            mLastItem = -1;
            mLastPage = 1;
            mBookGroupListAdapter.clear();
            loadMoreBookGroups();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGroupLoadTask != null) {
            mGroupLoadTask.cancel(true);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        BookGroup bookGroup = (BookGroup) getListView().getItemAtPosition(position);

        Intent i = new Intent(getActivity(), BookListActivity2.class);
        i.putExtra(BookListActivity2.EXTRA_BOOK_GROUP_TYPE, getBookGroupType());
        i.putExtra(BookListActivity2.EXTRA_BOOK_GROUP_ID, bookGroup.id);

        startActivity(i);
    }

    /**
     * Return the type of the book groups.
     *
     * @return BookGroupType.
     */
    protected abstract BookGroup.BookGroupType getBookGroupType();

    /**
     * Return the total number of book groups.
     *
     * @param db SQLiteDatabase.
     * @return Book group count.
     */
    protected abstract int getBookGroupCount(SQLiteDatabase db);

    /**
     * Load book groups.
     *
     * @param db     SQLiteDatabase.
     * @param limit  Limit.
     * @param offset Offset.
     * @return List of BookGroup.
     */
    protected abstract List<BookGroup> loadBookGroupList(SQLiteDatabase db, int limit, int offset);

    /**
     * Load more book groups.
     */
    private void loadMoreBookGroups() {
        mGroupLoadTask = new GroupLoadTask(GROUPS_BY_PAGE, GROUPS_BY_PAGE * (mLastPage - 1));
        mGroupLoadTask.execute();
        mLastPage++;
    }

    /**
     * Set the footer text.
     */
    private void setFooterText() {
        if (mBookGroupCount != null) {

            int displayedBookCount = mBookGroupListAdapter.getCount();

            Resources res = getResources();
            String footerText = res.getQuantityString(R.plurals.footer_fragment_books, displayedBookCount, displayedBookCount, mBookGroupCount);

            mTextViewFooter.setText(footerText);
        }
    }

    /**
     * A task to load book groups.
     */
    private final class GroupLoadTask extends AsyncTask<Void, Void, List<BookGroup>> {

        private final int mLimit;
        private final int mOffset;

        /**
         * Constructor.
         *
         * @param limit  The max number of groups to return.
         * @param offset Offset.
         */
        public GroupLoadTask(int limit, int offset) {
            mLimit = limit;
            mOffset = offset;
        }

        @Override
        protected List<BookGroup> doInBackground(Void... params) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
            mBookGroupCount = AbstractBookGroupListFragment.this.getBookGroupCount(db);
            return AbstractBookGroupListFragment.this.loadBookGroupList(db, mLimit, mOffset);
        }

        @Override
        protected void onPostExecute(List<BookGroup> bookGroups) {
            super.onPostExecute(bookGroups);

            int initialAdapterBookCount = mBookGroupListAdapter.getCount();

            mBookGroupListAdapter.addAll(bookGroups);
            mBookGroupListAdapter.notifyDataSetChanged();

            int bookCount = bookGroups.size();
            if (bookCount > 0 && initialAdapterBookCount > 0) {
                Resources res = getResources();
                String message = res.getQuantityString(R.plurals.message_books_loaded, bookCount, bookCount);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            setFooterText();
        }
    }
}
