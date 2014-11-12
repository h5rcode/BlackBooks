package com.blackbooks.fragments;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.search.BookSearcher;

/**
 * Fragment without user interface used to search book information.
 */
public class BookSearchFragment extends Fragment {

	private static final String ARG_ISBN = "ARG_ISBN";

	private BookSearchTask mBookSearchTask;

	private BookSearchListener mBookSearchListener;

	/**
	 * Return a new instance of BookSearchFragment, ready to search information
	 * from an ISBN number.
	 * 
	 * @param isbn
	 *            ISBN number.
	 * @return BookSearchFragment.
	 */
	public static BookSearchFragment newIntance(String isbn) {
		BookSearchFragment newInstance = new BookSearchFragment();
		Bundle args = new Bundle();
		args.putString(ARG_ISBN, isbn);
		newInstance.setArguments(args);
		return newInstance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mBookSearchListener = (BookSearchListener) activity;
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
		mBookSearchListener = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBookSearchTask != null) {
			mBookSearchTask.cancel(true);
		}
	}

	/**
	 * Activities hosting {@link BookSearchFragment} should implement this
	 * interface to be notified when the search is finshed.
	 */
	public interface BookSearchListener {

		/**
		 * Called when the search is finished.
		 * 
		 * @param bookInfo
		 *            BookInfo.
		 */
		void onSearchFinished(BookInfo bookInfo);
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
			} catch (JSONException e) {
				errorMessageId = R.string.error_json_exception;
			} catch (URISyntaxException e) {
				errorMessageId = R.string.error_uri_syntax;
			} catch (UnknownHostException e) {
				errorMessageId = R.string.error_connection_problem;
			} catch (IOException e) {
				throw new RuntimeException(e);
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
			if (mBookSearchListener != null) {
				mBookSearchListener.onSearchFinished(result);
			}
		}
	}
}
