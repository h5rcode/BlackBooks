package com.blackbooks.activities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByFirstLetterAdapter;
import com.blackbooks.adapters.FirstLetterItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.adapters.ListItemType;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;

public class BookListByFirstLetter extends ListActivity {

	private BooksByFirstLetterAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);

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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ListItem item = mAdapter.getItem(position);
		ListItemType itemType = item.getListItemType();

		if (itemType == ListItemType.Entry) {
			BookItem bookItem = (BookItem) item;
			Intent i = new Intent(BookListByFirstLetter.this, BookDisplay.class);
			i.putExtra(BookDisplay.EXTRA_BOOK_ID, bookItem.getId());
			BookListByFirstLetter.this.startActivity(i);
		}
	}
}
