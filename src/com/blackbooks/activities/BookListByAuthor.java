package com.blackbooks.activities;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;

import com.blackbooks.R;
import com.blackbooks.adapters.AuthorItem;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByAuthorAdapter;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.AuthorInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.AuthorServices;

/**
 * Activity that lists all the books.
 */
public final class BookListByAuthor extends AbstractBookList {

	private BooksByAuthorAdapter mAdapter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_list, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new BooksByAuthorAdapter(this);
		setListAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		ArrayList<ListItem> listItems = getList();
		mAdapter.clear();
		mAdapter.addAll(listItems);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Return the list of items to display.
	 * 
	 * @return ArrayList.
	 */
	private ArrayList<ListItem> getList() {
		SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<AuthorInfo> authorInfoList = AuthorServices.getAuthorInfoList(db);
		db.close();

		ArrayList<ListItem> listItems = new ArrayList<ListItem>();

		for (AuthorInfo authorInfo : authorInfoList) {
			if (authorInfo.id == null) {
				authorInfo.name = getString(R.string.label_unspecified_author);
			}
			AuthorItem authorItem = new AuthorItem(authorInfo.id, authorInfo.name, authorInfo.books.size());
			listItems.add(authorItem);

			for (Book book : authorInfo.books) {
				BookItem bookEntry = new BookItem(book.id, book.title, book.smallThumbnail);
				listItems.add(bookEntry);
			}
		}
		return listItems;
	}
}
