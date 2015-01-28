package com.blackbooks.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.search.BookSearcher;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Fragment without user interface used to search book information.
 */
public class IsbnLookupFragment extends Fragment {

    private static final String ARG_ISBN = "ARG_ISBN";

    private BookSearchTask mBookSearchTask;

    private IsbnLookupListener mIsbnLookupListener;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mIsbnLookupListener = (IsbnLookupListener) activity;
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
            BookInfo book = null;
            try {
                book = BookSearcher.search(barCode);
            } catch (ClientProtocolException e) {
                errorMessageId = R.string.error_connection_problem;
            } catch (HttpHostConnectException e) {
                errorMessageId = R.string.error_connection_problem;
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
                    Toast.makeText(getActivity(), getString(R.string.message_no_result), Toast.LENGTH_LONG).show();
                }
            }
            if (mIsbnLookupListener != null) {
                mIsbnLookupListener.onLookupFinished(result);
            }
        }
    }
}
