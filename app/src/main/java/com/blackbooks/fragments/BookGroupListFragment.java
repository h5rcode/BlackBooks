package com.blackbooks.fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.blackbooks.activities.BookListActivity2;
import com.blackbooks.adapters.BookGroupListAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.services.BookGroupServices;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * A fragment to display the book groups of a certain type.
 */
public final class BookGroupListFragment extends ListFragment {

    private static final String ARG_BOOK_GROUP_TYPE = "ARG_BOOK_GROUP_TYPE";
    private static final int GROUPS_BY_PAGE = 50;
    private BookGroup.BookGroupType mBookGroupType;
    private GroupLoadTask mGroupLoadTask;
    private int mLastPage = 1;
    private int mLastItem = -1;

    private BookGroupListAdapter mBookGroupListAdapter;

    /**
     * Return an instance of BookGroupListFragment initialized to display the groups of a certain type.
     *
     * @param bookGroupType Book group type.
     * @return BookGroupListFragment.
     */
    public static BookGroupListFragment newInstance(BookGroup.BookGroupType bookGroupType) {
        BookGroupListFragment fragment = new BookGroupListFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_GROUP_TYPE, bookGroupType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        mBookGroupType = (BookGroup.BookGroupType) args.getSerializable(ARG_BOOK_GROUP_TYPE);

        mBookGroupListAdapter = new BookGroupListAdapter(getActivity());
        setListAdapter(mBookGroupListAdapter);
        mGroupLoadTask = new GroupLoadTask(GROUPS_BY_PAGE, GROUPS_BY_PAGE * (mLastPage - 1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Do nothing.
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                switch (view.getId()) {
                    case android.R.id.list:

                        final int lastItem = firstVisibleItem + visibleItemCount;
                        if (lastItem == totalItemCount) {
                            if (mLastItem != lastItem) {
                                mLastItem = lastItem;
                                mGroupLoadTask = new GroupLoadTask(GROUPS_BY_PAGE, GROUPS_BY_PAGE * (mLastPage - 1));
                                mGroupLoadTask.execute();
                                mLastPage++;
                            }
                        }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VariableUtils.getInstance().getReloadBookGroupList()) {
            VariableUtils.getInstance().setReloadBookGroupListToFalse();
            mLastItem = -1;
            mLastPage = 1;
            mBookGroupListAdapter.clear();
            mBookGroupListAdapter.notifyDataSetInvalidated();
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
        i.putExtra(BookListActivity2.EXTRA_BOOK_GROUP_TYPE, mBookGroupType);
        i.putExtra(BookListActivity2.EXTRA_BOOK_GROUP_ID, bookGroup.id);

        startActivity(i);
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
            return BookGroupServices.getBookGroupList(db, mBookGroupType, mLimit, mOffset);
        }

        @Override
        protected void onPostExecute(List<BookGroup> categoryGroups) {
            super.onPostExecute(categoryGroups);
            mBookGroupListAdapter.addAll(categoryGroups);
        }
    }
}
