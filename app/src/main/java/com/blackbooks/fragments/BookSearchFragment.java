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
import android.widget.ListView;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.activities.BookDisplayActivity;
import com.blackbooks.adapters.BookSearchResultsAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.FullTextSearchServices;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * Book search fragment.
 */
public class BookSearchFragment extends ListFragment {

    private static final String ARGS_QUERY = "ARGS_QUERY";

    private View mEmptyView;
    private View mLoadingView;
    private ListView mListView;
    private TextView mTextFooter;

    private BookSearchResultsAdapter mAdapter;
    private String mQuery;
    private String mFooterText;

    private BookSearchTask mBookSearchTask;

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

        mEmptyView = view.findViewById(R.id.bookSearch_emptyView);
        mLoadingView = view.findViewById(R.id.bookSearch_loadingView);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mTextFooter = (TextView) view.findViewById(R.id.bookSearch_textFooter);

        mBookSearchTask = new BookSearchTask();
        mBookSearchTask.execute();

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
        if (VariableUtils.getInstance().getReloadBookList()) {
            mBookSearchTask = new BookSearchTask();
            mBookSearchTask.execute();
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
     * Implementation of AsyncTask used to search books without blocking the UI.
     */
    private class BookSearchTask extends AsyncTask<Void, Void, List<BookInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }

        @Override
        protected List<BookInfo> doInBackground(Void... params) {
            SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            List<BookInfo> bookList = FullTextSearchServices.searchBooks(db, mQuery);
            db.close();
            return bookList;
        }

        @Override
        protected void onPostExecute(List<BookInfo> result) {
            mAdapter.clear();
            mAdapter.addAll(result);
            mAdapter.notifyDataSetChanged();

            mLoadingView.setVisibility(View.GONE);
            if (result.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }

            Resources res = getResources();
            mFooterText = res.getQuantityString(R.plurals.label_footer_search, result.size(), result.size());
            mTextFooter.setText(mFooterText);
        }
    }
}
