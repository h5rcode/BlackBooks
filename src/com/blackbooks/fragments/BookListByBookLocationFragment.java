package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByBookLocationAdapter;
import com.blackbooks.adapters.BooksByBookLocationAdapter.BookLocationItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.BookLocationInfo;
import com.blackbooks.services.BookLocationServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * locations.
 */
public class BookListByBookLocationFragment extends AbstractBookListFragment {

	private String mActionBarSubtitle;

	@Override
	public String getActionBarSubtitle() {
		return mActionBarSubtitle;
	}

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByBookLocationAdapter(getActivity());
	}

	@Override
	protected List<ListItem> loadBookList() {
		SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
		SQLiteDatabase db = null;
		List<BookLocationInfo> bookLocationInfoList;
		try {
			db = dbHelper.getReadableDatabase();
			bookLocationInfoList = BookLocationServices.getBookLocationInfoList(db);
		} finally {
			if (db != null) {
				db.close();
			}
		}

		int bookLocationsCount = 0;

		List<ListItem> listItems = new ArrayList<ListItem>();
		for (BookLocationInfo bookLocationInfo : bookLocationInfoList) {
			if (bookLocationInfo.id == null) {
				bookLocationInfo.name = getString(R.string.label_unspecified_book_location);
			} else {
				bookLocationsCount++;
			}

			BookLocationItem bookLocationItem = new BookLocationItem(bookLocationInfo);
			listItems.add(bookLocationItem);

			for (BookInfo book : bookLocationInfo.books) {
				BookItem bookItem = new BookItem(book);
				listItems.add(bookItem);
			}
		}
		mActionBarSubtitle = String.format(getString(R.string.subtitle_fragment_books_by_book_location), bookLocationsCount);

		return listItems;
	}
}
