package com.blackbooks.fragments;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

/**
 * Fragment without user interfaces used to load a book from the database.
 */
public class BookLoadFragment extends Fragment {

	private static final String ARG_BOO_ID = "ARG_BOO_ID";

	private BookLoadListener mBookLoadListener;

	private BookLoadTask mBookLoadTask;

	/**
	 * Return a new instance of BookLoadFragment, initialized with the id of the
	 * book to load.
	 * 
	 * @param bookId
	 *            Id of the book to load.
	 * @return BookLoadFragment.
	 */
	public static BookLoadFragment newInstance(long bookId) {
		BookLoadFragment newInstance = new BookLoadFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_BOO_ID, bookId);
		newInstance.setArguments(args);
		return newInstance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mBookLoadListener = (BookLoadListener) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Bundle args = getArguments();
		long bookId = args.getLong(ARG_BOO_ID);
		mBookLoadTask = new BookLoadTask();
		mBookLoadTask.execute(bookId);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mBookLoadListener = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mBookLoadTask.cancel(true);
	}

	/**
	 * An activity hosting a {@link BookLoadFragment} should implement this
	 * interface to be notified when the book is loaded.
	 * 
	 */
	public interface BookLoadListener {

		/**
		 * Called when the book is loaded.
		 * 
		 * @param bookInfo
		 *            BookInfo.
		 */
		void onBookLoaded(BookInfo bookInfo);
	}

	/**
	 * Task used to load the information of a book.
	 */
	private final class BookLoadTask extends AsyncTask<Long, Void, BookInfo> {

		@Override
		protected BookInfo doInBackground(Long... params) {
			long bookId = params[0];

			SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			BookInfo bookInfo = BookServices.getBookInfo(db, bookId);
			db.close();
			return bookInfo;
		}

		@Override
		protected void onPostExecute(BookInfo result) {
			super.onPostExecute(result);
			if (mBookLoadListener != null) {
				mBookLoadListener.onBookLoaded(result);
			}
		}
	}
}
