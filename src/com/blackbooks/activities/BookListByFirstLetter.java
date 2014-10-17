package com.blackbooks.activities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByFirstLetterAdapter;
import com.blackbooks.adapters.FirstLetterItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;

/**
 * Activity that lists all the books, grouped by the first letter of their
 * title.
 */
public class BookListByFirstLetter extends AbstractBookList {

	private BooksByFirstLetterAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new BooksByFirstLetterAdapter(this);
		setListAdapter(mAdapter);
		loadBookList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (super.getReloadBookList()) {
			loadBookList();
			super.setReloadBookListToFalse();
		}
	}

	/**
	 * Return the list of items to display.
	 * 
	 * @return ArrayList.
	 */
	private ArrayList<ListItem> getList() {
		SQLiteHelper mDbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		ArrayList<Book> bookList = BookServices.getBookListMinimal(db);
		db.close();

		LinkedHashMap<String, ArrayList<Book>> bookMap = new LinkedHashMap<String, ArrayList<Book>>();

		for (Book book : bookList) {
			String firstLetter = book.title.substring(0, 1);

			if (!bookMap.containsKey(firstLetter)) {
				bookMap.put(firstLetter, new ArrayList<Book>());
			}
			bookMap.get(firstLetter).add(book);
		}

		ArrayList<ListItem> listItems = new ArrayList<ListItem>();
		for (String firstLetter : bookMap.keySet()) {
			FirstLetterItem firstLetterItem = new FirstLetterItem(firstLetter);
			listItems.add(firstLetterItem);

			for (Book book : bookMap.get(firstLetter)) {
				BookItem bookItem = new BookItem(book.id, book.title, book.smallThumbnail);
				listItems.add(bookItem);
			}
		}
		return listItems;
	}

	/**
	 * Load the book list and bind it to the list view.
	 */
	private void loadBookList() {
		ArrayList<ListItem> listItems = getList();
		mAdapter.clear();
		mAdapter.addAll(listItems);
		mAdapter.notifyDataSetChanged();
	}
}
