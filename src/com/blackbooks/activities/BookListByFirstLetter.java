package com.blackbooks.activities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByFirstLetterAdapter;
import com.blackbooks.adapters.FirstLetterItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;

public class BookListByFirstLetter extends AbstractBookList {

	private BooksByFirstLetterAdapter mAdapter;

	@Override
	protected void onResume() {
		super.onResume();

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

		mAdapter = new BooksByFirstLetterAdapter(this, listItems);
		setListAdapter(mAdapter);
	}
}
