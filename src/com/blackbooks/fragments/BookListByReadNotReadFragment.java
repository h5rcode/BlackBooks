package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByReadNotReadAdapter;
import com.blackbooks.adapters.BooksByReadNotReadAdapter.ReadNotReadItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

public class BookListByReadNotReadFragment extends AbstractBookListFragment {

	private String mFooterText;

	@Override
	protected List<ListItem> loadBookList() {

		SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		List<BookInfo> bookInfoList = BookServices.getBookInfoList(db);
		db.close();

		List<ListItem> listItems = new ArrayList<ListItem>();

		List<BookInfo> readBookList = new ArrayList<BookInfo>();
		List<BookInfo> unreadBookList = new ArrayList<BookInfo>();

		for (BookInfo bookInfo : bookInfoList) {
			if (bookInfo.isRead == 1L) {
				readBookList.add(bookInfo);
			} else {
				unreadBookList.add(bookInfo);
			}
		}

		if (!readBookList.isEmpty()) {
			ReadNotReadItem readItem = new ReadNotReadItem(true, readBookList.size());
			listItems.add(readItem);

			for (BookInfo bookInfo : readBookList) {
				BookItem bookItem = new BookItem(bookInfo);
				listItems.add(bookItem);
			}
		}

		if (!unreadBookList.isEmpty()) {
			ReadNotReadItem unreadItem = new ReadNotReadItem(false, unreadBookList.size());
			listItems.add(unreadItem);

			for (BookInfo bookInfo : unreadBookList) {
				BookItem bookItem = new BookItem(bookInfo);
				listItems.add(bookItem);
			}
		}

		int readCount = readBookList.size();
		int unreadCount = unreadBookList.size();
		int bookCount = bookInfoList.size();

		Resources res = getResources();
		String read = res.getQuantityString(R.plurals.label_footer_read, readCount, readCount);
		String unread = res.getQuantityString(R.plurals.label_footer_unread, unreadCount, unreadCount);
		String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);
		mFooterText = getString(R.string.footer_fragment_books_by_read_not_read, books, read, unread);

		return listItems;
	}

	@Override
	protected String getActionBarSubtitle() {
		return getString(R.string.action_sort_by_read_not_read);
	}

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByReadNotReadAdapter(getActivity());
	}

	@Override
	protected String getFooterText() {
		return mFooterText;
	}

}
