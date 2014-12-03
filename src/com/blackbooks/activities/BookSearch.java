package com.blackbooks.activities;

import java.util.List;

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
import com.blackbooks.services.FullTextSearchServices;
import com.blackbooks.utils.VariableUtils;

/**
 * The activity that searches books in the library.
 * 
 * TODO: Use a Fragment.
 */
public class BookSearch extends ListActivity {

	private BookSearchResultsAdapter mAdapter;
	private String mQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_book_search);
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mQuery = intent.getStringExtra(SearchManager.QUERY);
			mQuery = mQuery.replace("*", "");

			String title = String.format(getString(R.string.title_activity_book_search), mQuery);
			setTitle(title);

			List<BookInfo> bookList = searchBooks(mQuery);
			mAdapter = new BookSearchResultsAdapter(this, mQuery);
			mAdapter.addAll(bookList);
			setListAdapter(mAdapter);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (VariableUtils.getInstance().getReloadBookList()) {
			List<BookInfo> bookList = searchBooks(mQuery);
			mAdapter.clear();
			mAdapter.addAll(bookList);
			mAdapter.notifyDataSetChanged();
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

	/**
	 * Search the library for books whose title matches the query.
	 * 
	 * @param query
	 *            The query.
	 * @return List of {@link BookInfo}.
	 */
	private List<BookInfo> searchBooks(String query) {
		SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<BookInfo> bookList = FullTextSearchServices.searchBooks(db, query);
		db.close();
		return bookList;
	}
}
