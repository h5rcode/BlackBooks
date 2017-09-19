package com.blackbooks.fragments.isbnlookup;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.search.BookOnlineSearchService;
import com.blackbooks.utils.LogUtils;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Fragment without user interface used to search book information.
 */
public final class IsbnLookupFragment extends Fragment {

    private static final String ARG_ISBN = "ARG_ISBN";

    private BookSearchTask mBookSearchTask;

    private IsbnLookupListener mIsbnLookupListener;

    @Inject
    BookOnlineSearchService bookOnlineSearchService;

    /**
     * Return a new instance of IsbnLookupFragment, ready to search information
     * from an ISBN number.
     *
     * @param isbn ISBN number.
     * @return IsbnLookupFragment.
     */
    public static IsbnLookupFragment newInstance(String isbn) {
        IsbnLookupFragment newInstance = new IsbnLookupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN, isbn);
        newInstance.setArguments(args);
        return newInstance;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        mIsbnLookupListener = (IsbnLookupListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle args = getArguments();
        String isbn = args.getString(ARG_ISBN);
        if (isbn != null) {
            mBookSearchTask = new BookSearchTask();
            mBookSearchTask.execute(isbn);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIsbnLookupListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBookSearchTask != null) {
            mBookSearchTask.cancel(true);
        }
    }

    /**
     * Activities hosting {@link IsbnLookupFragment} should implement this
     * interface to be notified when the lookup is finished.
     */
    public interface IsbnLookupListener {

        /**
         * Called when the lookup is finished.
         *
         * @param bookInfo BookInfo.
         */
        void onLookupFinished(BookInfo bookInfo);
    }

    /**
     * Task performing the search for a book.
     */
    private final class BookSearchTask extends AsyncTask<String, Void, BookInfo> {

        private Integer errorMessageId = null;

        @Override
        protected BookInfo doInBackground(String... params) {
            String barCode = params[0];

            Log.i(LogUtils.TAG, String.format("Searching results for ISBN %s.", barCode));

            BookInfo book = null;
            try {
                book = bookOnlineSearchService.search(barCode);
            } catch (UnknownHostException e) {
                errorMessageId = R.string.error_connection_problem;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                // Do nothing. The method will terminate and return null.
            }
            return book;
        }

        @Override
        protected void onPostExecute(BookInfo result) {
            super.onPostExecute(result);
            if (result == null) {
                if (errorMessageId != null) {
                    Toast.makeText(getActivity(), errorMessageId, Toast.LENGTH_LONG).show();
                } else {
                    Log.i(LogUtils.TAG, "Search finished successfully but returned no results.");
                    Toast.makeText(getActivity(), getString(R.string.message_no_result), Toast.LENGTH_LONG).show();
                }
            } else {
                Log.i(LogUtils.TAG, String.format("Search finished successfully. Result: %s", result.title));
            }
            if (mIsbnLookupListener != null) {
                mIsbnLookupListener.onLookupFinished(result);
            }
        }
    }
}
