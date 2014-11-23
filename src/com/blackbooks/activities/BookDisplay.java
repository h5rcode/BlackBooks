package com.blackbooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BookDisplayFragment;
import com.blackbooks.fragments.BookDisplayFragment.BookDisplayListener;
import com.blackbooks.model.nonpersistent.BookInfo;

/**
 * Activity to display the info of a book saved in the database.
 */
public final class BookDisplay extends FragmentActivity implements BookDisplayListener {

	/**
	 * Key of the book id when passed as an extra of the activity.
	 */
	public final static String EXTRA_BOOK_ID = "EXTRA_BOOK_ID";

	private final static String BOOK_DISPLAY_FRAGMENT_TAG = "BOOK_DISPLAY_FRAGMENT_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
		setContentView(R.layout.activity_book_display);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		FragmentManager fm = getSupportFragmentManager();
		BookDisplayFragment fragment = (BookDisplayFragment) fm.findFragmentByTag(BOOK_DISPLAY_FRAGMENT_TAG);
		if (fragment == null) {
			Intent intent = this.getIntent();
			if (intent != null && intent.hasExtra(EXTRA_BOOK_ID)) {
				long bookId = intent.getLongExtra(EXTRA_BOOK_ID, Long.MIN_VALUE);
				fragment = BookDisplayFragment.newInstance(bookId);

				fm.beginTransaction() //
						.add(R.id.bookDisplay_frameLayout, fragment, BOOK_DISPLAY_FRAGMENT_TAG) //
						.commit();
			} else {
				this.finish();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
		case android.R.id.home:
			result = true;
			finish();
			break;
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}

	@Override
	public void onBookLoaded(BookInfo bookInfo) {
		this.setTitle(bookInfo.title);
	}

	@Override
	public void onBookDeleted() {
		this.finish();
	}
}
