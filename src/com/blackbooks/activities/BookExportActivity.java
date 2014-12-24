package com.blackbooks.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.blackbooks.R;
import com.blackbooks.fragments.BookExportFragment;

/**
 * An activity to export books.
 */
public class BookExportActivity extends AbstractDrawerActivity {

	private static final String BOOK_EXPORT_FRAGMENT_TAG = "BOOK_EXPORT_FRAGMENT_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

		FragmentManager fm = getSupportFragmentManager();

		BookExportFragment bookExportFragment = (BookExportFragment) fm.findFragmentByTag(BOOK_EXPORT_FRAGMENT_TAG);

		if (bookExportFragment == null) {
			bookExportFragment = new BookExportFragment();

			fm.beginTransaction() //
					.add(R.id.abstractDrawerActivity_frameLayout, bookExportFragment, BOOK_EXPORT_FRAGMENT_TAG) //
					.commit();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
	}
}
