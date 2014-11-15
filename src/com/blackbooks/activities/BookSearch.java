package com.blackbooks.activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.blackbooks.R;
import com.blackbooks.adapters.BookSearchResultsAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

/**
 * The activity that searches books in the library.
 */
public class BookSearch extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_book_search);
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			String title = String.format(getString(R.string.title_activity_book_search), query);
			setTitle(title);

			ArrayList<BookInfo> bookList = searchBooks(query);

			BookSearchResultsAdapter adapter = new BookSearchResultsAdapter(this, query);
			adapter.addAll(bookList);
			setListAdapter(adapter);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
		case android.R.id.home:
			result = true;
			NavUtils.navigateUpFromSameTask(this);
			break;

		default:
			result = super.onOptionsItemSelected(item);
		}
		return result;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		BookInfo bookInfo = (BookInfo) getListAdapter().getItem(position);
		Intent i = new Intent(this, BookDisplay.class);
		i.putExtra(BookDisplay.EXTRA_BOOK_ID, bookInfo.id);
		this.startActivity(i);
	}

	private ArrayList<BookInfo> searchBooks(String query) {
		SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<BookInfo> bookList = BookServices.searchBooks(db, query);
		db.close();
		return bookList;
	}
}
