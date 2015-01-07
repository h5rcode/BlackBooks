package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
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
 * location.
 */
public class BookListByBookLocationFragment extends AbstractBookListFragment {

	private String mFooterText;

	@Override
	protected String getActionBarSubtitle() {
		return getString(R.string.subtitle_fragment_books_by_book_location);
	}

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByBookLocationAdapter(getActivity());
	}

	@Override
	protected String getFooterText() {
		return mFooterText;
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

		List<ListItem> listItems = new ArrayList<ListItem>();

		int locationCount = 0;
		List<Long> bookIdList = new ArrayList<Long>();
		for (BookLocationInfo bookLocationInfo : bookLocationInfoList) {
			if (bookLocationInfo.id == null) {
				bookLocationInfo.name = getString(R.string.label_unspecified_book_location);
			} else {
				locationCount++;
			}

			BookLocationItem bookLocationItem = new BookLocationItem(bookLocationInfo);
			listItems.add(bookLocationItem);

			for (BookInfo book : bookLocationInfo.books) {
				BookItem bookItem = new BookItem(book);
				listItems.add(bookItem);
				
				if (!bookIdList.contains(book.id)) {
					bookIdList.add(book.id);
				}
			}
		}

		Resources res = getResources();
		int bookCount = bookIdList.size();
		String bookLocations = res.getQuantityString(R.plurals.label_footer_book_locations, locationCount, locationCount);
		String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);

		mFooterText = getString(R.string.footer_fragment_books_by_book_location, bookLocations, books);

		return listItems;
	}
}
