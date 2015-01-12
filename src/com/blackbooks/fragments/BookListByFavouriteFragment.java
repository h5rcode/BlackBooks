package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByFavouriteAdapter;
import com.blackbooks.adapters.BooksByFavouriteAdapter.FavouriteItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books in
 * two groups: "Favourites" or "Other books".
 */
public class BookListByFavouriteFragment extends AbstractBookListFragment {

	private String mFooterText;

	@Override
	protected String getActionBarSubtitle() {
		return getString(R.string.action_sort_by_favourite);
	}

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByFavouriteAdapter(getActivity());
	}

	@Override
	protected String getFooterText() {
		return mFooterText;
	}

	@Override
	protected List<ListItem> loadBookList() {

		SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		List<BookInfo> bookInfoList = BookServices.getBookInfoList(db);
		db.close();

		List<ListItem> listItems = new ArrayList<ListItem>();

		List<BookInfo> otherBookList = new ArrayList<BookInfo>();
		List<BookInfo> favouriteBookList = new ArrayList<BookInfo>();

		for (BookInfo bookInfo : bookInfoList) {
			if (bookInfo.isFavourite == 1L) {
				favouriteBookList.add(bookInfo);
			} else {
				otherBookList.add(bookInfo);
			}
		}

		if (!favouriteBookList.isEmpty()) {
			FavouriteItem readItem = new FavouriteItem(true, favouriteBookList.size());
			listItems.add(readItem);

			for (BookInfo bookInfo : favouriteBookList) {
				BookItem bookItem = new BookItem(bookInfo);
				listItems.add(bookItem);
			}
		}

		if (!otherBookList.isEmpty()) {
			FavouriteItem toReadItem = new FavouriteItem(false, otherBookList.size());
			listItems.add(toReadItem);

			for (BookInfo bookInfo : otherBookList) {
				BookItem bookItem = new BookItem(bookInfo);
				listItems.add(bookItem);
			}
		}

		int favouriteCount = favouriteBookList.size();
		int bookCount = bookInfoList.size();

		Resources res = getResources();
		String favourite = res.getQuantityString(R.plurals.label_footer_favourite, favouriteCount, favouriteCount);
		String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);
		mFooterText = getString(R.string.footer_fragment_books_by_favourite, books, favourite);

		return listItems;
	}
}
