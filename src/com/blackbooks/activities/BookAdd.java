package com.blackbooks.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BookAddFragment;
import com.blackbooks.fragments.BookAddFragment.BookAddListener;

/**
 * Activity used to add a book to the library.
 */
public final class BookAdd extends Activity implements BookAddListener {

	public final static String EXTRA_ISBN = "ISBN";

	private static final String BOOK_ADD_FRAGMENT_TAG = "BOOK_ADD_FRAGMENT_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		FragmentManager fm = getFragmentManager();
		BookAddFragment fragment = (BookAddFragment) fm.findFragmentByTag(BOOK_ADD_FRAGMENT_TAG);
		if (fragment == null) {
			String isbn = null;
			Intent intent = this.getIntent();
			if (intent != null && intent.hasExtra(EXTRA_ISBN)) {
				isbn = intent.getStringExtra(EXTRA_ISBN);
			}

			fragment = BookAddFragment.newInstance(isbn);

			fm.beginTransaction() //
					.add(R.id.bookAdd_frameLayout, fragment, BOOK_ADD_FRAGMENT_TAG) //
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			result = true;
			break;

		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}

	@Override
	public void onSaved() {
		NavUtils.navigateUpFromSameTask(this);
	}
}
