package com.blackbooks.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookDisplayActivity;
import com.blackbooks.adapters.BookSearchResultsAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.FullTextSearchServices;
import com.blackbooks.utils.StringUtils;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * Book search fragment.
 */
public class BookSearchFragment extends ListFragment {

    private static final String ARGS_QUERY = "ARGS_QUERY";

    private static final int RESULTS_BY_PAGE = 25;

    private TextView mTextFooter;

    private Integer mBookCount;
    private BookSearchResultsAdapter mAdapter;
    private String mQuery;
    private String mFooterText;

    private boolean mAlreadyLoaded;
    private int mLastPage = 1;
    private int mLastItem = -1;

    private BookSearchTask mBookSearchTask;

    /**
     * Return a new instance of BookSearchFragment that is initialized to perform a search with
     * a given query.
     *
     * @param query Query.
     * @return BookSearchFragment.
     */
    public static BookSearchFragment newInstance(String query) {
        BookSearchFragment bookSearchFragment = new BookSearchFragment();

        Bundle args = new Bundle();
        args.putString(ARGS_QUERY, query);
        bookSearchFragment.setArguments(args);

        return bookSearchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = this.getArguments();
        mQuery = args.getString(ARGS_QUERY);

        mAdapter = new BookSearchResultsAdapter(getActivity(), mQuery);
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search, container, false);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        mTextFooter = (TextView) view.findViewById(R.id.bookSearch_textFooter);

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
                                loadMoreBooks();
                            }
                        }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTextFooter.setText(mFooterText);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BookInfo bookInfo = (BookInfo) getListAdapter().getItem(position);
        Intent i = new Intent(getActivity(), BookDisplayActivity.class);
        i.putExtra(BookDisplayActivity.EXTRA_BOOK_ID, bookInfo.id);
        this.startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mAlreadyLoaded || VariableUtils.getInstance().getReloadBookList()) {
            mAlreadyLoaded = true;
            VariableUtils.getInstance().setReloadBookList(false);
            mLastItem = -1;
            mLastPage = 1;
            mAdapter.clear();
            loadMoreBooks();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBookSearchTask != null) {
            mBookSearchTask.cancel(true);
        }
    }

    /**
     * Load more books.
     */
    private void loadMoreBooks() {
        mBookSearchTask = new BookSearchTask(RESULTS_BY_PAGE, RESULTS_BY_PAGE * (mLastPage - 1));
        mBookSearchTask.execute();
        mLastPage++;
    }

    /**
     * Implementation of AsyncTask used to search books without blocking the UI.
     */
    private class BookSearchTask extends AsyncTask<Void, Void, List<BookInfo>> {

        private final int mLimit;
        private final int mOffset;

        /**
         * Constructor.
         *
         * @param limit  Max number of books to load.
         * @param offset The offset when loading books.
         */
        public BookSearchTask(int limit, int offset) {
            super();
            mLimit = limit;
            mOffset = offset;
        }

        @Override
        protected List<BookInfo> doInBackground(Void... params) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();

            String query = StringUtils.normalize(mQuery);
            query += "*";
            mBookCount = FullTextSearchServices.getSearchResultCount(db, query);
            return FullTextSearchServices.searchBooks(db, query, mLimit, mOffset);
        }

        @Override
        protected void onPostExecute(List<BookInfo> result) {

            int initialAdapterBookCount = mAdapter.getCount();

            mAdapter.addAll(result);
            mAdapter.notifyDataSetChanged();

            Resources res = getResources();
            int resultCount = result.size();
            if (resultCount > 0 && initialAdapterBookCount > 0) {
                String message = res.getQuantityString(R.plurals.message_results_loaded, resultCount, resultCount);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
            int loadedResultCount = mAdapter.getCount();
            mFooterText = res.getQuantityString(R.plurals.label_footer_search, loadedResultCount, loadedResultCount, mBookCount);
            mTextFooter.setText(mFooterText);
        }
    }
}
