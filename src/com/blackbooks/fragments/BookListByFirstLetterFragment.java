package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByFirstLetterAdapter;
import com.blackbooks.adapters.FirstLetterItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.services.BookServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * the first letter of their title.
 */
public class BookListByFirstLetterFragment extends AbstractBookListFragment {

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByFirstLetterAdapter(this.getActivity());
	}

	/**
	 * Return the list of items to display.
	 * 
	 * @return ArrayList.
	 */
	@Override
	protected ArrayList<ListItem> loadBookList() {
		SQLiteHelper mDbHelper = new SQLiteHelper(this.getActivity());
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		ArrayList<BookInfo> bookList = BookServices.getBookInfoList(db);
		db.close();

		LinkedHashMap<String, ArrayList<BookInfo>> bookMap = new LinkedHashMap<String, ArrayList<BookInfo>>();

		for (BookInfo book : bookList) {
			String firstLetter = book.title.substring(0, 1);

			if (!bookMap.containsKey(firstLetter)) {
				bookMap.put(firstLetter, new ArrayList<BookInfo>());
			}
			bookMap.get(firstLetter).add(book);
		}

		ArrayList<ListItem> listItems = new ArrayList<ListItem>();
		for (String firstLetter : bookMap.keySet()) {
			FirstLetterItem firstLetterItem = new FirstLetterItem(firstLetter);
			listItems.add(firstLetterItem);

			for (BookInfo book : bookMap.get(firstLetter)) {
				BookItem bookItem = new BookItem(book.id, book.title, book.smallThumbnail);
				for (Author author : book.authors) {
					bookItem.getAuthors().add(author.name);
				}
				listItems.add(bookItem);
			}
		}
		return listItems;
	}
}
