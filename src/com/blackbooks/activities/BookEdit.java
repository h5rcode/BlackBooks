package com.blackbooks.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BookEditFragment;
import com.blackbooks.fragments.BookEditFragment.BookEditListener;

/**
 * Activity used to add a new book or edit an existing one.
 */
public final class BookEdit extends Activity implements BookEditListener {

	public static final String EXTRA_BOOK_ID = "EXTRA_BOOK_ID";
	public static final String EXTRA_MODE = "EXTRA_MODE";
	public static final String EXTRA_ISBN = "EXTRA_ISBN";

	public static final int MODE_ADD = 1;
	public static final int MODE_EDIT = 2;

	private static final String BOOK_ADD_FRAGMENT_TAG = "BOOK_ADD_FRAGMENT_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_edit);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		FragmentManager fm = getFragmentManager();
		BookEditFragment fragment = (BookEditFragment) fm.findFragmentByTag(BOOK_ADD_FRAGMENT_TAG);
		if (fragment == null) {
			Intent intent = this.getIntent();

			int mode = intent.getIntExtra(EXTRA_MODE, 0);

			switch (mode) {
			case MODE_ADD:
				String isbn = null;
				if (intent.hasExtra(EXTRA_ISBN)) {
					isbn = intent.getStringExtra(EXTRA_ISBN);
				}
				fragment = BookEditFragment.newInstanceAddMode(isbn);
				break;

			case MODE_EDIT:
				if (intent.hasExtra(EXTRA_BOOK_ID)) {
					long booId = intent.getLongExtra(EXTRA_BOOK_ID, 0);
					fragment = BookEditFragment.newInstanceEditMode(booId);
				} else {
					throw new IllegalStateException("Extra " + EXTRA_BOOK_ID + " not set.");
				}
				break;

			default:
				throw new IllegalStateException("Extra " + EXTRA_MODE + " not set");
			}

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
