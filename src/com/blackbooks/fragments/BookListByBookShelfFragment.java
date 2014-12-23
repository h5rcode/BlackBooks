package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BookShelfItem;
import com.blackbooks.adapters.BooksByBookShelfAdapter;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.BookShelfInfo;
import com.blackbooks.services.BookShelfServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * shelves.
 */
public class BookListByBookShelfFragment extends AbstractBookListFragment {

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByBookShelfAdapter(getActivity());
	}

	@Override
	protected List<ListItem> loadBookList() {
		SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
		SQLiteDatabase db = null;
		List<BookShelfInfo> bookShelfInfoList;
		try {
			db = dbHelper.getReadableDatabase();
			bookShelfInfoList = BookShelfServices.getBookShelfInfoList(db);
		} finally {
			if (db != null) {
				db.close();
			}
		}

		List<ListItem> listItems = new ArrayList<ListItem>();

		for (BookShelfInfo bookShelfInfo : bookShelfInfoList) {
			if (bookShelfInfo.id == null) {
				bookShelfInfo.name = getString(R.string.label_unspecified_bookshelf);
			}

			BookShelfItem bookShelfItem = new BookShelfItem(bookShelfInfo);
			listItems.add(bookShelfItem);

			for (BookInfo book : bookShelfInfo.books) {
				BookItem bookItem = new BookItem(book);
				listItems.add(bookItem);
			}
		}

		return listItems;
	}
}
